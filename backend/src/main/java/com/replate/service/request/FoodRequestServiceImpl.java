package com.replate.service.request;

import com.replate.domain.entity.FoodDonation;
import com.replate.domain.entity.FoodRequest;
import com.replate.domain.entity.NgoProfile;
import com.replate.domain.enums.DonationStatus;
import com.replate.domain.enums.RequestStatus;
import com.replate.dto.request.CreateFoodRequestDTO;
import com.replate.dto.response.FoodRequestResponseDTO;
import com.replate.exception.*;
import com.replate.repository.FoodDonationRepository;
import com.replate.repository.FoodRequestRepository;
import com.replate.repository.NgoProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class FoodRequestServiceImpl implements FoodRequestService {

    private final FoodRequestRepository foodRequestRepository;
    private final FoodDonationRepository foodDonationRepository;
    private final NgoProfileRepository ngoProfileRepository;

    public FoodRequestServiceImpl(FoodRequestRepository foodRequestRepository,
                                  FoodDonationRepository foodDonationRepository,
                                  NgoProfileRepository ngoProfileRepository) {
        this.foodRequestRepository = foodRequestRepository;
        this.foodDonationRepository = foodDonationRepository;
        this.ngoProfileRepository = ngoProfileRepository;
    }

    @Override
    @Transactional
    public FoodRequestResponseDTO createRequest(CreateFoodRequestDTO requestDTO, Long ngoUserId) {
        // Step 1: Ensure requester has a verified NgoProfile
        NgoProfile ngo = ngoProfileRepository.findById(ngoUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NgoProfile", "userId", ngoUserId));

        // Step 2: Validate target FoodDonation
        FoodDonation donation = foodDonationRepository.findById(requestDTO.getDonationId())
                .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", "id", requestDTO.getDonationId()));

        // Donation availability status check
        if (donation.getStatus() != DonationStatus.AVAILABLE && donation.getStatus() != DonationStatus.PARTIALLY_ALLOCATED) {
            throw new ReplateException("Cannot request from a food donation with status: " + donation.getStatus() +
                    ". Only AVAILABLE batches accept requests.");
        }

        // Expiry check
        if (donation.isExpired()) {
            throw new DonationExpiredException("Cannot request an expired food donation batch.");
        }

        // Quantity validation
        if (requestDTO.getRequestedQuantity() == null || requestDTO.getRequestedQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReplateException("Requested quantity must be strictly greater than zero.");
        }

        if (requestDTO.getRequestedQuantity().compareTo(donation.getAvailableQuantity()) > 0) {
            throw new InsufficientQuantityException(requestDTO.getRequestedQuantity(), donation.getAvailableQuantity());
        }

        // Duplicate active request check: Same NGO cannot have multiple PENDING requests for the same batch
        if (foodRequestRepository.existsByDonationIdAndNgoUserIdAndStatus(donation.getId(), ngoUserId, RequestStatus.PENDING)) {
            throw new ReplateException("You already have a pending request for this food donation. " +
                    "Please wait for the donor's decision or withdraw your current request before submitting another.");
        }

        // Step 3: Construct and persist FoodRequest
        // NOTE: Step 5 Critical Boundary: Request submission does NOT deduct stock or modify DonationStatus.
        FoodRequest foodRequest = new FoodRequest();
        foodRequest.setDonation(donation);
        foodRequest.setNgo(ngo);
        foodRequest.setRequestedQuantity(requestDTO.getRequestedQuantity());
        foodRequest.setIntendedBeneficiaries(requestDTO.getIntendedBeneficiaries().trim());
        foodRequest.setStatus(RequestStatus.PENDING);

        FoodRequest saved = foodRequestRepository.save(foodRequest);
        return FoodRequestResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodRequestResponseDTO getRequestById(Long requestId, Long currentUserId, String currentUserRole) {
        FoodRequest request = foodRequestRepository.findByIdWithDetails(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodRequest", "id", requestId));

        boolean isRequesterNgo = currentUserId != null && request.getNgo().getUserId().equals(currentUserId);
        boolean isOwnerDonor = currentUserId != null && request.getDonation().getDonor().getUserId().equals(currentUserId);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        if (!isRequesterNgo && !isOwnerDonor && !isAdmin) {
            throw new UnauthorizedRoleException("You do not have permission to view this food request.");
        }

        return FoodRequestResponseDTO.fromEntity(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodRequestResponseDTO> getMyRequests(Long ngoUserId, Pageable pageable) {
        return foodRequestRepository.findByNgoUserId(ngoUserId, pageable)
                .map(FoodRequestResponseDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodRequestResponseDTO> getReceivedRequests(Long donorUserId, Pageable pageable) {
        return foodRequestRepository.findByDonorUserId(donorUserId, pageable)
                .map(FoodRequestResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public FoodRequestResponseDTO approveRequest(Long requestId, Long donorUserId, String remarks) {
        FoodRequest request = foodRequestRepository.findByIdWithDetails(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodRequest", "id", requestId));

        // Ownership enforcement: Only the donor who posted the donation can approve
        if (!request.getDonation().getDonor().getUserId().equals(donorUserId)) {
            throw new UnauthorizedRoleException("Only the donor who posted this food donation can approve incoming requests.");
        }

        // Already processed check
        if (!request.isPendingDecision()) {
            throw new ReplateException("Food request has already been processed with status: " + request.getStatus());
        }

        // NOTE: Step 5 Critical Boundary: Request approval records donor approval without deducting stock.
        // Full atomic allocation and inventory deduction occur in the Allocation engine.
        request.approve(donorUserId, remarks);
        FoodRequest updated = foodRequestRepository.save(request);

        return FoodRequestResponseDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public FoodRequestResponseDTO rejectRequest(Long requestId, Long donorUserId, String rejectionReason) {
        FoodRequest request = foodRequestRepository.findByIdWithDetails(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodRequest", "id", requestId));

        // Ownership enforcement
        if (!request.getDonation().getDonor().getUserId().equals(donorUserId)) {
            throw new UnauthorizedRoleException("Only the donor who posted this food donation can reject incoming requests.");
        }

        // Already processed check
        if (!request.isPendingDecision()) {
            throw new ReplateException("Food request has already been processed with status: " + request.getStatus());
        }

        String reason = (rejectionReason != null && !rejectionReason.trim().isBlank())
                ? rejectionReason.trim()
                : "Request declined by food donor.";

        request.reject(donorUserId, reason);
        FoodRequest updated = foodRequestRepository.save(request);

        return FoodRequestResponseDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public FoodRequestResponseDTO withdrawRequest(Long requestId, Long ngoUserId) {
        FoodRequest request = foodRequestRepository.findByIdWithDetails(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodRequest", "id", requestId));

        // Ownership enforcement: Only the requesting NGO can withdraw
        if (!request.getNgo().getUserId().equals(ngoUserId)) {
            throw new UnauthorizedRoleException("Only the requesting NGO can withdraw this food request.");
        }

        // Already processed check
        if (!request.isPendingDecision()) {
            throw new ReplateException("Cannot withdraw a food request that has already been " + request.getStatus());
        }

        request.withdraw(ngoUserId);
        FoodRequest updated = foodRequestRepository.save(request);

        return FoodRequestResponseDTO.fromEntity(updated);
    }
}
