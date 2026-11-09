package com.replate.controller;

import com.replate.domain.enums.DietaryType;
import com.replate.domain.enums.FoodCategory;
import com.replate.dto.request.CreateDonationRequestDTO;
import com.replate.dto.request.UpdateDonationRequestDTO;
import com.replate.dto.response.ApiResponse;
import com.replate.dto.response.DonationResponseDTO;
import com.replate.dto.response.PageResponseDTO;
import com.replate.security.UserPrincipal;
import com.replate.service.donation.FoodDonationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/donations")
public class FoodDonationController {

    private final FoodDonationService foodDonationService;

    public FoodDonationController(FoodDonationService foodDonationService) {
        this.foodDonationService = foodDonationService;
    }

    /**
     * Create a new food donation listing (DRAFT or AVAILABLE).
     * Accessible by verified DONOR or ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<DonationResponseDTO>> createDonation(
            @Valid @RequestBody CreateDonationRequestDTO request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DonationResponseDTO response = foodDonationService.createDonation(request, currentUser.getId());
        String message = request.isPublishImmediately()
                ? "Food donation published successfully and is now active for NGO requests."
                : "Food donation draft saved successfully.";

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, message));
    }

    /**
     * Update an existing food donation listing.
     * Allowed only for DRAFT or AVAILABLE listings owned by the authenticated donor.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<DonationResponseDTO>> updateDonation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDonationRequestDTO request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DonationResponseDTO response = foodDonationService.updateDonation(id, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Food donation updated successfully."));
    }

    /**
     * Publish a DRAFT food donation listing to make it AVAILABLE for NGO claims.
     */
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<DonationResponseDTO>> publishDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DonationResponseDTO response = foodDonationService.publishDonation(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Food donation published successfully."));
    }

    /**
     * Cancel an active or draft food donation listing.
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<DonationResponseDTO>> cancelDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DonationResponseDTO response = foodDonationService.cancelDonation(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Food donation cancelled."));
    }

    /**
     * Retrieve a donor's own listings with pagination.
     */
    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponseDTO<DonationResponseDTO>>> getMyDonations(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50));
        Page<DonationResponseDTO> resultPage = foodDonationService.getMyDonations(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponseDTO.fromPage(resultPage)));
    }

    /**
     * Public search and discovery endpoint for active, unexpired food batches.
     */
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<PageResponseDTO<DonationResponseDTO>>> searchPublicDonations(
            @RequestParam(required = false) FoodCategory category,
            @RequestParam(required = false) DietaryType dietaryType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50));
        Page<DonationResponseDTO> resultPage = foodDonationService.searchPublicDonations(
                category, dietaryType, city, query, pageable);

        return ResponseEntity.ok(ApiResponse.success(PageResponseDTO.fromPage(resultPage)));
    }

    /**
     * Public view of a single published donation.
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<ApiResponse<DonationResponseDTO>> getPublicDonationById(@PathVariable Long id) {
        DonationResponseDTO donation = foodDonationService.getDonationById(id, null, null);
        return ResponseEntity.ok(ApiResponse.success(donation));
    }

    /**
     * Authenticated view of a donation by ID (enforcing draft ownership visibility).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DonationResponseDTO>> getDonationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long userId = currentUser != null ? currentUser.getId() : null;
        String userRole = currentUser != null ? currentUser.getRole().name() : null;

        DonationResponseDTO donation = foodDonationService.getDonationById(id, userId, userRole);
        return ResponseEntity.ok(ApiResponse.success(donation));
    }
}
