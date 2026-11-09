package com.replate.service.donation;

import com.replate.domain.enums.DietaryType;
import com.replate.domain.enums.FoodCategory;
import com.replate.dto.request.CreateDonationRequestDTO;
import com.replate.dto.request.UpdateDonationRequestDTO;
import com.replate.dto.response.DonationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FoodDonationService {

    DonationResponseDTO createDonation(CreateDonationRequestDTO request, Long donorUserId);

    DonationResponseDTO updateDonation(Long donationId, UpdateDonationRequestDTO request, Long donorUserId);

    DonationResponseDTO publishDonation(Long donationId, Long donorUserId);

    DonationResponseDTO cancelDonation(Long donationId, Long donorUserId);

    DonationResponseDTO getDonationById(Long donationId, Long currentUserId, String currentUserRole);

    Page<DonationResponseDTO> getMyDonations(Long donorUserId, Pageable pageable);

    Page<DonationResponseDTO> searchPublicDonations(FoodCategory category, DietaryType dietaryType, String city, String query, Pageable pageable);

    int refreshExpiredDonations();
}

