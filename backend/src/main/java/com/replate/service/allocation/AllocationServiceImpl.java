package com.replate.service.allocation;

import com.replate.domain.entity.Allocation;
import com.replate.domain.entity.BaseUser;
import com.replate.domain.entity.FoodDonation;
import com.replate.domain.entity.FoodRequest;
import com.replate.domain.enums.AllocationStatus;
import com.replate.domain.enums.DonationStatus;
import com.replate.domain.enums.RequestStatus;
import com.replate.dto.request.CreateAllocationDTO;
import com.replate.dto.response.AllocationResponseDTO;
import com.replate.exception.DonationExpiredException;
import com.replate.exception.InsufficientQuantityException;
import com.replate.exception.ReplateException;
import com.replate.exception.ResourceNotFoundException;
import com.replate.repository.AllocationRepository;
import com.replate.repository.FoodDonationRepository;
import com.replate.repository.FoodRequestRepository;
import com.replate.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AllocationServiceImpl implements AllocationService {

    private static final Logger log = LoggerFactory.getLogger(AllocationServiceImpl.class);

    private final AllocationRepository allocationRepository;
    private final FoodDonationRepository foodDonationRepository;
    private final FoodRequestRepository foodRequestRepository;
    private final UserRepository userRepository;

    public AllocationServiceImpl(
            AllocationRepository allocationRepository,
            FoodDonationRepository foodDonationRepository,
            FoodRequestRepository foodRequestRepository,
            UserRepository userRepository) {
        this.allocationRepository = allocationRepository;
        this.foodDonationRepository = foodDonationRepository;
        this.foodRequestRepository = foodRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AllocationResponseDTO allocateFood(CreateAllocationDTO dto, Long actorUserId, String actorRole) {
        log.info("Processing allocation request by user ID: {} (role: {}) for request ID: {}",
                actorUserId, actorRole, dto.getRequestId());

        // 1. Acquire pessimistic write lock on the food request
        FoodRequest request = foodRequestRepository.findByIdWithPessimisticLock(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Food request not found with ID: " + dto.getRequestId()));

        // 2. Validate request status: only APPROVED requests can be allocated
        if (request.getStatus() != RequestStatus.APPROVED) {
            throw new ReplateException(
                    "Only APPROVED food requests can receive inventory allocation. Current request status: " + request.getStatus(),
                    "INVALID_REQUEST_STATUS");
        }

        // 3. Acquire pessimistic write lock on the food donation
        FoodDonation donation = foodDonationRepository.findByIdWithPessimisticLock(request.getDonation().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Target food donation not found with ID: " + request.getDonation().getId()));

        // 4. Authorize actor: only the owning commercial donor (or Admin) can authorize allocation
        boolean isAdmin = "ADMIN".equalsIgnoreCase(actorRole) || "ROLE_ADMIN".equalsIgnoreCase(actorRole);
        if (!isAdmin) {
            if (donation.getDonor() == null || !donation.getDonor().getUserId().equals(actorUserId)) {
                throw new AccessDeniedException("Only the commercial donor who owns this donation listing can authorize stock allocation.");
            }
        }

        // 5. Revalidate donation availability state inside locked transaction
        if (donation.isExpired()) {
            throw new DonationExpiredException("Cannot allocate from an expired surplus food donation batch.");
        }

        if (donation.getStatus() != DonationStatus.AVAILABLE && donation.getStatus() != DonationStatus.PARTIALLY_ALLOCATED) {
            throw new ReplateException(
                    "Food donation cannot receive allocations in current status: " + donation.getStatus() +
                    ". Only AVAILABLE or PARTIALLY_ALLOCATED batches can allocate stock.",
                    "DONATION_NOT_AVAILABLE");
        }

        if (donation.getAvailableQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientQuantityException(
                    "Donation inventory is completely exhausted (0.00 " + donation.getQuantityUnit() + " remaining).");
        }

        // 6. Recalculate request's remaining allocatable quantity
        BigDecimal alreadyAllocatedForRequest = allocationRepository.sumAllocatedQuantityByRequestId(request.getId());
        BigDecimal remainingRequestQty = request.getRequestedQuantity().subtract(alreadyAllocatedForRequest);

        if (remainingRequestQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReplateException(
                    String.format("Food request ID %d has already been fully allocated (%.2f of %.2f %s).",
                            request.getId(), alreadyAllocatedForRequest, request.getRequestedQuantity(), donation.getQuantityUnit()),
                    "REQUEST_ALREADY_FULFILLED");
        }

        // 7. Determine target allocation quantity
        BigDecimal targetAllocQuantity;
        if (dto.getAllocatedQuantity() != null) {
            BigDecimal requestedQty = dto.getAllocatedQuantity();
            if (requestedQty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ReplateException("Allocation quantity must be strictly greater than zero.", "INVALID_QUANTITY");
            }
            if (requestedQty.compareTo(remainingRequestQty) > 0) {
                throw new InsufficientQuantityException(
                        String.format("Requested allocation quantity (%.2f) exceeds the request's remaining unallocated balance (%.2f %s).",
                                requestedQty, remainingRequestQty, donation.getQuantityUnit()));
            }
            if (requestedQty.compareTo(donation.getAvailableQuantity()) > 0) {
                throw new InsufficientQuantityException(
                        String.format("Requested allocation quantity (%.2f) exceeds the donation's currently available inventory (%.2f %s).",
                                requestedQty, donation.getAvailableQuantity(), donation.getQuantityUnit()));
            }
            targetAllocQuantity = requestedQty;
        } else {
            // Default: Allocate minimum of remaining request quantity and available donation stock
            targetAllocQuantity = remainingRequestQty.min(donation.getAvailableQuantity());
            if (targetAllocQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InsufficientQuantityException("No available inventory to allocate.");
            }
        }

        // 8. Prevent overselling donation batch total inventory
        BigDecimal currentTotalAllocatedForDonation = allocationRepository.sumAllocatedQuantityByDonationId(donation.getId());
        if (currentTotalAllocatedForDonation.add(targetAllocQuantity).compareTo(donation.getTotalQuantity()) > 0) {
            throw new InsufficientQuantityException(
                    String.format("Allocation of %.2f would cause cumulative allocations (%.2f) to exceed original batch total (%.2f %s).",
                            targetAllocQuantity, currentTotalAllocatedForDonation.add(targetAllocQuantity),
                            donation.getTotalQuantity(), donation.getQuantityUnit()));
        }

        // 9. Atomic inventory deduction and status transition on FoodDonation
        boolean deductionSuccess = donation.deductQuantity(targetAllocQuantity);
        if (!deductionSuccess) {
            throw new InsufficientQuantityException("Failed to deduct allocation quantity from donation inventory.");
        }
        foodDonationRepository.save(donation);

        // 10. Audit tracking: capture actor identity
        BaseUser actorUser = userRepository.findById(actorUserId).orElse(null);

        // 11. Create and persist Allocation record
        Allocation allocation = new Allocation(request, donation, targetAllocQuantity, actorUser);
        allocation.setStatus(AllocationStatus.AWAITING_PICKUP);
        Allocation savedAllocation = allocationRepository.save(allocation);

        log.info("Successfully created allocation ID: {} for {} {} from donation ID: {} to NGO ID: {}",
                savedAllocation.getId(), targetAllocQuantity, donation.getQuantityUnit(),
                donation.getId(), request.getNgo().getUserId());

        return AllocationResponseDTO.fromEntity(
                allocationRepository.findByIdWithDetails(savedAllocation.getId()).orElse(savedAllocation));
    }

    @Override
    @Transactional(readOnly = true)
    public AllocationResponseDTO getAllocationById(Long id, Long actorUserId, String actorRole) {
        Allocation allocation = allocationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation record not found with ID: " + id));

        boolean isAdmin = "ADMIN".equalsIgnoreCase(actorRole) || "ROLE_ADMIN".equalsIgnoreCase(actorRole);
        if (!isAdmin) {
            Long donorUserId = allocation.getDonation().getDonor().getUserId();
            Long ngoUserId = allocation.getRequest().getNgo().getUserId();
            if (!actorUserId.equals(donorUserId) && !actorUserId.equals(ngoUserId)) {
                throw new AccessDeniedException("You are not authorized to view this food allocation record.");
            }
        }

        return AllocationResponseDTO.fromEntity(allocation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllocationResponseDTO> getAllocationsByDonation(Long donationId, Long actorUserId, String actorRole) {
        FoodDonation donation = foodDonationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Food donation not found with ID: " + donationId));

        boolean isAdmin = "ADMIN".equalsIgnoreCase(actorRole) || "ROLE_ADMIN".equalsIgnoreCase(actorRole);
        if (!isAdmin) {
            if (donation.getDonor() == null || !donation.getDonor().getUserId().equals(actorUserId)) {
                throw new AccessDeniedException("You are not authorized to view allocations for this donation.");
            }
        }

        return allocationRepository.findByDonationId(donationId).stream()
                .map(AllocationResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllocationResponseDTO> getAllocationsByRequest(Long requestId, Long actorUserId, String actorRole) {
        FoodRequest request = foodRequestRepository.findByIdWithDetails(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Food request not found with ID: " + requestId));

        boolean isAdmin = "ADMIN".equalsIgnoreCase(actorRole) || "ROLE_ADMIN".equalsIgnoreCase(actorRole);
        if (!isAdmin) {
            Long donorUserId = request.getDonation().getDonor().getUserId();
            Long ngoUserId = request.getNgo().getUserId();
            if (!actorUserId.equals(donorUserId) && !actorUserId.equals(ngoUserId)) {
                throw new AccessDeniedException("You are not authorized to view allocations for this food request.");
            }
        }

        return allocationRepository.findByRequestId(requestId).stream()
                .map(AllocationResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AllocationResponseDTO> getMyAllocations(Long actorUserId, String actorRole, Pageable pageable) {
        if ("DONOR".equalsIgnoreCase(actorRole) || "ROLE_DONOR".equalsIgnoreCase(actorRole)) {
            return allocationRepository.findByDonorUserId(actorUserId, pageable)
                    .map(AllocationResponseDTO::fromEntity);
        } else if ("NGO".equalsIgnoreCase(actorRole) || "ROLE_NGO".equalsIgnoreCase(actorRole)) {
            return allocationRepository.findByNgoUserId(actorUserId, pageable)
                    .map(AllocationResponseDTO::fromEntity);
        } else if ("ADMIN".equalsIgnoreCase(actorRole) || "ROLE_ADMIN".equalsIgnoreCase(actorRole)) {
            return allocationRepository.findAll(pageable)
                    .map(AllocationResponseDTO::fromEntity);
        } else {
            throw new AccessDeniedException("Role " + actorRole + " does not have an allocation console.");
        }
    }
}
