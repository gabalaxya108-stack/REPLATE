package com.replate.service.donation;

import com.replate.domain.entity.DonorProfile;
import com.replate.domain.entity.FoodDonation;
import com.replate.domain.enums.DietaryType;
import com.replate.domain.enums.DonationStatus;
import com.replate.domain.enums.FoodCategory;
import com.replate.dto.request.CreateDonationRequestDTO;
import com.replate.dto.request.UpdateDonationRequestDTO;
import com.replate.dto.response.DonationResponseDTO;
import com.replate.exception.DonationExpiredException;
import com.replate.exception.ReplateException;
import com.replate.exception.ResourceNotFoundException;
import com.replate.exception.UnauthorizedRoleException;
import com.replate.repository.DonorProfileRepository;
import com.replate.repository.FoodDonationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class FoodDonationServiceImpl implements FoodDonationService {

    private final FoodDonationRepository foodDonationRepository;
    private final DonorProfileRepository donorProfileRepository;

    public FoodDonationServiceImpl(FoodDonationRepository foodDonationRepository,
                                  DonorProfileRepository donorProfileRepository) {
        this.foodDonationRepository = foodDonationRepository;
        this.donorProfileRepository = donorProfileRepository;
    }

    @Override
    @Transactional
    public DonationResponseDTO createDonation(CreateDonationRequestDTO request, Long donorUserId) {
        DonorProfile donor = donorProfileRepository.findById(donorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("DonorProfile", "userId", donorUserId));

        // Validation Rule 1: Expiry must be strictly after preparation time
        if (request.getExpiryTime().isBefore(request.getPreparationTime()) ||
            request.getExpiryTime().equals(request.getPreparationTime())) {
            throw new ReplateException("Expiry time must be strictly after preparation time.");
        }

        // Validation Rule 2: Total quantity must be positive
        if (request.getTotalQuantity() == null || request.getTotalQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReplateException("Total quantity must be strictly greater than zero.");
        }

        // Determine initial status based on publish flag
        DonationStatus initialStatus;
        if (request.isPublishImmediately()) {
            if (Instant.now().isAfter(request.getExpiryTime())) {
                throw new DonationExpiredException("Cannot publish a food donation with an expiry time in the past.");
            }
            initialStatus = DonationStatus.AVAILABLE;
        } else {
            initialStatus = DonationStatus.DRAFT;
        }

        FoodDonation donation = new FoodDonation();
        donation.setDonor(donor);
        donation.setTitle(request.getTitle().trim());
        donation.setCategory(request.getCategory());
        donation.setTotalQuantity(request.getTotalQuantity());
        donation.setAvailableQuantity(request.getTotalQuantity());
        donation.setQuantityUnit(request.getQuantityUnit());
        donation.setDietaryType(request.getDietaryType());
        donation.setStorageCondition(request.getStorageCondition());
        donation.setPreparationTime(request.getPreparationTime());
        donation.setExpiryTime(request.getExpiryTime());
        donation.setPickupNotes(request.getPickupNotes() != null ? request.getPickupNotes().trim() : null);
        donation.setStatus(initialStatus);

        FoodDonation saved = foodDonationRepository.save(donation);
        return DonationResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public DonationResponseDTO updateDonation(Long donationId, UpdateDonationRequestDTO request, Long donorUserId) {
        FoodDonation donation = foodDonationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", "id", donationId));

        // Ownership enforcement
        if (!donation.getDonor().getUserId().equals(donorUserId)) {
            throw new UnauthorizedRoleException("You do not have permission to modify this food donation listing.");
        }

        // State machine rule: only DRAFT and AVAILABLE can be edited by donor
        if (!donation.canBeEditedByDonor()) {
            throw new ReplateException("Donation cannot be edited in current status: " + donation.getStatus());
        }

        // Validation: Expiry must be after preparation time
        if (request.getExpiryTime().isBefore(request.getPreparationTime()) ||
            request.getExpiryTime().equals(request.getPreparationTime())) {
            throw new ReplateException("Expiry time must be strictly after preparation time.");
        }

        // If listing is already published (AVAILABLE), expiry must not be in the past
        if (donation.getStatus() == DonationStatus.AVAILABLE && Instant.now().isAfter(request.getExpiryTime())) {
            throw new DonationExpiredException("Cannot set expiry time in the past for an active available donation.");
        }

        // Quantity preservation rule: Total quantity cannot be less than already allocated stock
        BigDecimal allocatedStock = donation.getTotalQuantity().subtract(donation.getAvailableQuantity());
        if (request.getTotalQuantity().compareTo(allocatedStock) < 0) {
            throw new ReplateException("Total quantity cannot be reduced below already allocated quantity of " +
                    allocatedStock + " " + donation.getQuantityUnit().getLabel());
        }

        // Update fields
        donation.setTitle(request.getTitle().trim());
        donation.setCategory(request.getCategory());
        donation.setTotalQuantity(request.getTotalQuantity());
        donation.setAvailableQuantity(request.getTotalQuantity().subtract(allocatedStock));
        donation.setQuantityUnit(request.getQuantityUnit());
        donation.setDietaryType(request.getDietaryType());
        donation.setStorageCondition(request.getStorageCondition());
        donation.setPreparationTime(request.getPreparationTime());
        donation.setExpiryTime(request.getExpiryTime());
        donation.setPickupNotes(request.getPickupNotes() != null ? request.getPickupNotes().trim() : null);

        FoodDonation updated = foodDonationRepository.save(donation);
        return DonationResponseDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public DonationResponseDTO publishDonation(Long donationId, Long donorUserId) {
        FoodDonation donation = foodDonationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", "id", donationId));

        if (!donation.getDonor().getUserId().equals(donorUserId)) {
            throw new UnauthorizedRoleException("You do not have permission to publish this food donation.");
        }

        donation.publish();
        FoodDonation published = foodDonationRepository.save(donation);
        return DonationResponseDTO.fromEntity(published);
    }

    @Override
    @Transactional
    public DonationResponseDTO cancelDonation(Long donationId, Long donorUserId) {
        FoodDonation donation = foodDonationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", "id", donationId));

        if (!donation.getDonor().getUserId().equals(donorUserId)) {
            throw new UnauthorizedRoleException("You do not have permission to cancel this food donation.");
        }

        donation.cancel();
        FoodDonation cancelled = foodDonationRepository.save(donation);
        return DonationResponseDTO.fromEntity(cancelled);
    }

    @Override
    @Transactional(readOnly = true)
    public DonationResponseDTO getDonationById(Long donationId, Long currentUserId, String currentUserRole) {
        FoodDonation donation = foodDonationRepository.findByIdWithDonorDetails(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodDonation", "id", donationId));

        // Privacy rule: Draft donations can only be viewed by owning donor or platform admin
        if (donation.getStatus() == DonationStatus.DRAFT) {
            boolean isOwner = currentUserId != null && donation.getDonor().getUserId().equals(currentUserId);
            boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);
            if (!isOwner && !isAdmin) {
                throw new UnauthorizedRoleException("Draft listings are private and only viewable by the owning donor.");
            }
        }

        return DonationResponseDTO.fromEntity(donation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DonationResponseDTO> getMyDonations(Long donorUserId, Pageable pageable) {
        return foodDonationRepository.findByDonorUserId(donorUserId, pageable)
                .map(DonationResponseDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DonationResponseDTO> searchPublicDonations(FoodCategory category, DietaryType dietaryType,
                                                          String city, String query, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<FoodDonation> spec = (root, q, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            predicates.add(cb.equal(root.get("status"), DonationStatus.AVAILABLE));
            predicates.add(cb.greaterThan(root.get("expiryTime"), Instant.now()));

            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (dietaryType != null) {
                predicates.add(cb.equal(root.get("dietaryType"), dietaryType));
            }
            if (city != null && !city.trim().isBlank() && !"ALL".equalsIgnoreCase(city)) {
                predicates.add(cb.equal(
                        cb.lower(root.join("donor").join("user").get("location").get("city")),
                        city.trim().toLowerCase()
                ));
            }
            if (query != null && !query.trim().isBlank()) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                jakarta.persistence.criteria.Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                jakarta.persistence.criteria.Predicate orgMatch = cb.like(cb.lower(root.join("donor").get("organizationName")), pattern);
                predicates.add(cb.or(titleMatch, orgMatch));
            }

            if (q != null) {
                q.orderBy(cb.desc(root.get("createdAt")));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return foodDonationRepository.findAll(spec, pageable).map(DonationResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public int refreshExpiredDonations() {
        List<FoodDonation> expiredActive = foodDonationRepository.findExpiredActiveDonations(Instant.now());
        for (FoodDonation donation : expiredActive) {
            donation.setStatus(DonationStatus.EXPIRED);
        }
        foodDonationRepository.saveAll(expiredActive);
        return expiredActive.size();
    }
}
