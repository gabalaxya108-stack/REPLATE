package com.replate.service.allocation;

import com.replate.dto.request.CreateAllocationDTO;
import com.replate.dto.response.AllocationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AllocationService {

    /**
     * Executes atomic inventory allocation fulfilling an approved NGO food request.
     * Enforces pessimistic locking, concurrency serialization, stock deduction, and status transitions.
     */
    AllocationResponseDTO allocateFood(CreateAllocationDTO dto, Long actorUserId, String actorRole);

    /**
     * Retrieves single allocation details with strict authorization checks.
     */
    AllocationResponseDTO getAllocationById(Long id, Long actorUserId, String actorRole);

    /**
     * Retrieves all allocation records associated with a specific food donation listing.
     */
    List<AllocationResponseDTO> getAllocationsByDonation(Long donationId, Long actorUserId, String actorRole);

    /**
     * Retrieves all allocation records fulfilling a specific food request.
     */
    List<AllocationResponseDTO> getAllocationsByRequest(Long requestId, Long actorUserId, String actorRole);

    /**
     * Retrieves paginated allocations relevant to the authenticated user (Donor, NGO, or Admin).
     */
    Page<AllocationResponseDTO> getMyAllocations(Long actorUserId, String actorRole, Pageable pageable);
}
