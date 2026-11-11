package com.replate.service.delivery;

import com.replate.dto.request.*;
import com.replate.dto.response.DeliveryTaskResponseDTO;
import com.replate.dto.response.VolunteerSummaryDTO;

import java.util.List;

public interface DeliveryService {

    DeliveryTaskResponseDTO createDeliveryTask(Long allocationId, Long actorUserId);

    DeliveryTaskResponseDTO assignVolunteer(AssignVolunteerDTO dto, Long actorUserId);

    DeliveryTaskResponseDTO acceptAssignment(Long deliveryId, Long volunteerUserId);

    DeliveryTaskResponseDTO rejectAssignment(Long deliveryId, Long volunteerUserId, RejectAssignmentDTO dto);

    DeliveryTaskResponseDTO confirmPickup(Long deliveryId, Long volunteerUserId, PickupDeliveryDTO dto);

    DeliveryTaskResponseDTO startTransit(Long deliveryId, Long volunteerUserId, StartTransitDTO dto);

    DeliveryTaskResponseDTO completeDelivery(Long deliveryId, Long volunteerUserId, CompleteDeliveryDTO dto);

    DeliveryTaskResponseDTO reportIssue(Long deliveryId, Long volunteerUserId, ReportIssueDTO dto);

    DeliveryTaskResponseDTO getDeliveryById(Long deliveryId, Long actorUserId);

    DeliveryTaskResponseDTO getDeliveryByAllocationId(Long allocationId, Long actorUserId);

    List<DeliveryTaskResponseDTO> getMyDeliveries(Long actorUserId);

    List<DeliveryTaskResponseDTO> getAvailableTasks();

    List<VolunteerSummaryDTO> getAvailableVolunteers();
}
