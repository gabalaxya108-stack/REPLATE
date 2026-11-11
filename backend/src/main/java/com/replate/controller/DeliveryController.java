package com.replate.controller;

import com.replate.dto.request.*;
import com.replate.dto.response.ApiResponse;
import com.replate.dto.response.DeliveryTaskResponseDTO;
import com.replate.dto.response.VolunteerSummaryDTO;
import com.replate.security.UserPrincipal;
import com.replate.service.delivery.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * Dispatch an unassigned delivery task for a confirmed allocation.
     * Authorized: DONOR, ADMIN.
     */
    @PostMapping("/allocation/{allocationId}/dispatch")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryTaskResponseDTO>> dispatchDelivery(
            @PathVariable Long allocationId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DeliveryTaskResponseDTO response = deliveryService.createDeliveryTask(allocationId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Delivery task generated successfully. Awaiting volunteer assignment."));
    }

    /**
     * Assign an eligible volunteer to an allocation run.
     * Authorized: DONOR, ADMIN.
     */
    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryTaskResponseDTO>> assignVolunteer(
            @Valid @RequestBody AssignVolunteerDTO dto,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DeliveryTaskResponseDTO response = deliveryService.assignVolunteer(dto, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Volunteer assigned successfully to delivery task."));
    }

    /**
     * Volunteer accepts assignment.
     * Authorized: VOLUNTEER, ADMIN.
     */
    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryTaskResponseDTO>> acceptAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DeliveryTaskResponseDTO response = deliveryService.acceptAssignment(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Delivery run accepted. Proceed to donor kitchen for pickup."));
    }

    /**
     * Volunteer rejects assignment, releasing run for reassignment.
     * Authorized: VOLUNTEER, ADMIN.
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryTaskResponseDTO>> rejectAssignment(
            @PathVariable Long id,
            @RequestBody(required = false) RejectAssignmentDTO dto,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DeliveryTaskResponseDTO response = deliveryService.rejectAssignment(id, currentUser.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Assignment rejected. Run released for reassignment."));
    }

    /**
     * Confirm food pickup from donor kitchen with optional verification PIN and notes.
     * Authorized: VOLUNTEER, ADMIN.
     */
    @PostMapping("/{id}/pickup")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryTaskResponseDTO>> confirmPickup(
            @PathVariable Long id,
            @RequestBody(required = false) PickupDeliveryDTO dto,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DeliveryTaskResponseDTO response = deliveryService.confirmPickup(id, currentUser.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Food pickup verified successfully. Package ready for transit."));
    }

    /**
     * Volunteer starts transit to NGO distribution site.
     * Authorized: VOLUNTEER, ADMIN.
     */
    @PostMapping("/{id}/start-transit")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryTaskResponseDTO>> startTransit(
            @PathVariable Long id,
            @RequestBody(required = false) StartTransitDTO dto,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DeliveryTaskResponseDTO response = deliveryService.startTransit(id, currentUser.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Package transit initiated. En route to NGO site."));
    }

    /**
     * Complete delivery at NGO site with recipient acknowledgment and delivery PIN.
     * Authorized: VOLUNTEER, ADMIN.
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryTaskResponseDTO>> completeDelivery(
            @PathVariable Long id,
            @RequestBody(required = false) CompleteDeliveryDTO dto,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DeliveryTaskResponseDTO response = deliveryService.completeDelivery(id, currentUser.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Delivery confirmed successfully. Thank you for your service!"));
    }

    /**
     * Report an issue with the delivery run.
     * Authorized: VOLUNTEER, DONOR, NGO, ADMIN.
     */
    @PostMapping("/{id}/report-issue")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DeliveryTaskResponseDTO>> reportIssue(
            @PathVariable Long id,
            @Valid @RequestBody ReportIssueDTO dto,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DeliveryTaskResponseDTO response = deliveryService.reportIssue(id, currentUser.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Issue reported. Support team notified."));
    }

    /**
     * Retrieve single delivery task details by ID.
     * Authorized: VOLUNTEER, DONOR, NGO, ADMIN.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DeliveryTaskResponseDTO>> getDeliveryById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DeliveryTaskResponseDTO response = deliveryService.getDeliveryById(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Delivery details retrieved successfully."));
    }

    /**
     * Retrieve delivery task details by allocation ID.
     * Authorized: VOLUNTEER, DONOR, NGO, ADMIN.
     */
    @GetMapping("/allocation/{allocationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DeliveryTaskResponseDTO>> getDeliveryByAllocationId(
            @PathVariable Long allocationId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DeliveryTaskResponseDTO response = deliveryService.getDeliveryByAllocationId(allocationId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Delivery details retrieved successfully."));
    }

    /**
     * Get deliveries relevant to the authenticated user.
     * Authorized: VOLUNTEER, DONOR, NGO, ADMIN.
     */
    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<DeliveryTaskResponseDTO>>> getMyDeliveries(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<DeliveryTaskResponseDTO> response = deliveryService.getMyDeliveries(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "User delivery tasks retrieved successfully."));
    }

    /**
     * Get unassigned / available delivery runs for volunteers to browse.
     * Authorized: VOLUNTEER, ADMIN.
     */
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<DeliveryTaskResponseDTO>>> getAvailableTasks() {
        List<DeliveryTaskResponseDTO> response = deliveryService.getAvailableTasks();
        return ResponseEntity.ok(ApiResponse.success(response, "Available delivery runs retrieved."));
    }

    /**
     * List active eligible volunteers for assignment.
     * Authorized: DONOR, ADMIN.
     */
    @GetMapping("/available-volunteers")
    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VolunteerSummaryDTO>>> getAvailableVolunteers() {
        List<VolunteerSummaryDTO> response = deliveryService.getAvailableVolunteers();
        return ResponseEntity.ok(ApiResponse.success(response, "Available volunteers retrieved."));
    }
}
