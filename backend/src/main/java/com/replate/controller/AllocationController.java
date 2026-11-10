package com.replate.controller;

import com.replate.dto.request.CreateAllocationDTO;
import com.replate.dto.response.AllocationResponseDTO;
import com.replate.dto.response.ApiResponse;
import com.replate.dto.response.PageResponseDTO;
import com.replate.security.UserPrincipal;
import com.replate.service.allocation.AllocationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/allocations")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    /**
     * Create an allocation committing surplus food inventory against an approved NGO food request.
     * Authorized for owning commercial Donors and Admins.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<AllocationResponseDTO>> createAllocation(
            @Valid @RequestBody CreateAllocationDTO requestDTO,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        AllocationResponseDTO response = allocationService.allocateFood(
                requestDTO, currentUser.getId(), currentUser.getRole().name());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Food stock allocated successfully. Dispatch awaiting volunteer assignment."));
    }

    /**
     * Retrieve paginated allocations relevant to the authenticated user.
     * Donors view outgoing allocations; NGOs view incoming allocations.
     */
    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('DONOR', 'NGO', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponseDTO<AllocationResponseDTO>>> getMyAllocations(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50));
        Page<AllocationResponseDTO> resultPage = allocationService.getMyAllocations(
                currentUser.getId(), currentUser.getRole().name(), pageable);

        return ResponseEntity.ok(ApiResponse.success(PageResponseDTO.fromPage(resultPage)));
    }

    /**
     * Retrieve a single allocation record by its ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AllocationResponseDTO>> getAllocationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        AllocationResponseDTO response = allocationService.getAllocationById(
                id, currentUser.getId(), currentUser.getRole().name());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retrieve all allocations created against a specific food donation listing.
     */
    @GetMapping("/donation/{donationId}")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<AllocationResponseDTO>>> getAllocationsByDonation(
            @PathVariable Long donationId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<AllocationResponseDTO> allocations = allocationService.getAllocationsByDonation(
                donationId, currentUser.getId(), currentUser.getRole().name());

        return ResponseEntity.ok(ApiResponse.success(allocations));
    }

    /**
     * Retrieve all allocations fulfilling a specific approved food request.
     */
    @GetMapping("/request/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<AllocationResponseDTO>>> getAllocationsByRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<AllocationResponseDTO> allocations = allocationService.getAllocationsByRequest(
                requestId, currentUser.getId(), currentUser.getRole().name());

        return ResponseEntity.ok(ApiResponse.success(allocations));
    }
}
