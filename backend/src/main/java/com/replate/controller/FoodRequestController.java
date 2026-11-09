package com.replate.controller;

import com.replate.dto.request.CreateFoodRequestDTO;
import com.replate.dto.request.RejectFoodRequestDTO;
import com.replate.dto.response.ApiResponse;
import com.replate.dto.response.FoodRequestResponseDTO;
import com.replate.dto.response.PageResponseDTO;
import com.replate.security.UserPrincipal;
import com.replate.service.request.FoodRequestService;
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
@RequestMapping("/api/v1/requests")
public class FoodRequestController {

    private final FoodRequestService foodRequestService;

    public FoodRequestController(FoodRequestService foodRequestService) {
        this.foodRequestService = foodRequestService;
    }

    /**
     * Submit a new food redistribution claim for an available donation.
     * Accessible by verified NGOs.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    public ResponseEntity<ApiResponse<FoodRequestResponseDTO>> createRequest(
            @Valid @RequestBody CreateFoodRequestDTO requestDTO,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        FoodRequestResponseDTO response = foodRequestService.createRequest(requestDTO, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Food request submitted successfully and is awaiting donor review."));
    }

    /**
     * Retrieve all requests submitted by the authenticated NGO.
     */
    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponseDTO<FoodRequestResponseDTO>>> getMyRequests(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50));
        Page<FoodRequestResponseDTO> resultPage = foodRequestService.getMyRequests(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponseDTO.fromPage(resultPage)));
    }

    /**
     * Retrieve all incoming requests received for the authenticated donor's donations.
     */
    @GetMapping("/received")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponseDTO<FoodRequestResponseDTO>>> getReceivedRequests(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50));
        Page<FoodRequestResponseDTO> resultPage = foodRequestService.getReceivedRequests(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponseDTO.fromPage(resultPage)));
    }

    /**
     * Retrieve single request details by ID (requester NGO, donor, or admin).
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FoodRequestResponseDTO>> getRequestById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        FoodRequestResponseDTO response = foodRequestService.getRequestById(
                id, currentUser.getId(), currentUser.getRole().name());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Approve an incoming food request (by owning donor).
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<FoodRequestResponseDTO>> approveRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        FoodRequestResponseDTO response = foodRequestService.approveRequest(id, currentUser.getId(), remarks);
        return ResponseEntity.ok(ApiResponse.success(response, "Food request approved. Prepared for allocation and dispatch."));
    }

    /**
     * Reject an incoming food request with justification (by owning donor).
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<FoodRequestResponseDTO>> rejectRequest(
            @PathVariable Long id,
            @Valid @RequestBody RejectFoodRequestDTO rejectDTO,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        FoodRequestResponseDTO response = foodRequestService.rejectRequest(id, currentUser.getId(), rejectDTO.getRejectionReason());
        return ResponseEntity.ok(ApiResponse.success(response, "Food request declined."));
    }

    /**
     * Withdraw a pending request (by requesting NGO).
     */
    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasAnyRole('NGO', 'ADMIN')")
    public ResponseEntity<ApiResponse<FoodRequestResponseDTO>> withdrawRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        FoodRequestResponseDTO response = foodRequestService.withdrawRequest(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Food request withdrawn."));
    }
}
