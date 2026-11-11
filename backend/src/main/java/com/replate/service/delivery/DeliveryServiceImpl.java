package com.replate.service.delivery;

import com.replate.domain.entity.*;
import com.replate.domain.enums.*;
import com.replate.dto.request.*;
import com.replate.dto.response.DeliveryTaskResponseDTO;
import com.replate.dto.response.VolunteerSummaryDTO;
import com.replate.exception.*;
import com.replate.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final DeliveryTaskRepository deliveryTaskRepository;
    private final AllocationRepository allocationRepository;
    private final VolunteerProfileRepository volunteerProfileRepository;
    private final UserRepository userRepository;
    private final FoodDonationRepository foodDonationRepository;

    public DeliveryServiceImpl(DeliveryTaskRepository deliveryTaskRepository,
                               AllocationRepository allocationRepository,
                               VolunteerProfileRepository volunteerProfileRepository,
                               UserRepository userRepository,
                               FoodDonationRepository foodDonationRepository) {
        this.deliveryTaskRepository = deliveryTaskRepository;
        this.allocationRepository = allocationRepository;
        this.volunteerProfileRepository = volunteerProfileRepository;
        this.userRepository = userRepository;
        this.foodDonationRepository = foodDonationRepository;
    }

    private String generateOtp() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DeliveryTaskResponseDTO createDeliveryTask(Long allocationId, Long actorUserId) {
        Allocation allocation = allocationRepository.findByIdWithDetails(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found with ID: " + allocationId));

        if (allocation.getStatus() == AllocationStatus.CANCELLED) {
            throw new InvalidDeliveryStateException("Cannot create delivery task for an unconfirmed or cancelled allocation.");
        }

        // Authorization check: must be donating merchant or admin
        BaseUser actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + actorUserId));

        boolean isDonor = allocation.getDonation().getDonor().getUserId().equals(actorUserId);
        boolean isAdmin = actor.getRole() == UserRole.ADMIN;
        if (!isDonor && !isAdmin) {
            throw new UnauthorizedRoleException("Only the donating merchant or an administrator can dispatch delivery tasks.");
        }

        // Prevent duplicate active delivery tasks
        if (deliveryTaskRepository.existsByAllocationId(allocationId)) {
            DeliveryTask existing = deliveryTaskRepository.findByAllocationId(allocationId).orElse(null);
            if (existing != null && existing.getStatus() != DeliveryStatus.CANCELLED && existing.getStatus() != DeliveryStatus.FAILED) {
                throw new ReplateException("An active delivery task already exists for this allocation.", "DUPLICATE_DELIVERY_TASK");
            }
        }

        String pickupOtp = generateOtp();
        String deliveryOtp = generateOtp();

        DeliveryTask task = new DeliveryTask(allocation, pickupOtp, deliveryOtp);
        task.setStatus(DeliveryStatus.UNASSIGNED);
        DeliveryTask saved = deliveryTaskRepository.save(task);

        log.info("Created delivery task ID: {} for allocation ID: {}", saved.getId(), allocationId);
        return DeliveryTaskResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DeliveryTaskResponseDTO assignVolunteer(AssignVolunteerDTO dto, Long actorUserId) {
        Allocation allocation = allocationRepository.findByIdWithDetails(dto.getAllocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found with ID: " + dto.getAllocationId()));

        if (allocation.getStatus() == AllocationStatus.CANCELLED) {
            throw new InvalidDeliveryStateException("Cannot assign delivery for a cancelled allocation.");
        }

        // Authorization guard: Donor owner or ADMIN
        BaseUser actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + actorUserId));

        boolean isDonor = allocation.getDonation().getDonor().getUserId().equals(actorUserId);
        boolean isAdmin = actor.getRole() == UserRole.ADMIN;
        if (!isDonor && !isAdmin) {
            throw new UnauthorizedRoleException("Only the donating merchant or an administrator can assign volunteers to delivery tasks.");
        }

        // Validate volunteer
        VolunteerProfile volunteer = volunteerProfileRepository.findByIdWithUser(dto.getVolunteerId())
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer profile not found with ID: " + dto.getVolunteerId()));

        if (volunteer.getUser() == null || !"ACTIVE".equals(volunteer.getUser().getStatus()) || volunteer.getUser().getRole() != UserRole.VOLUNTEER) {
            throw new ReplateException("Cannot assign delivery task to an inactive or non-volunteer user.", "INACTIVE_VOLUNTEER");
        }

        // Find or create DeliveryTask
        DeliveryTask task = deliveryTaskRepository.findByAllocationId(dto.getAllocationId()).orElse(null);
        if (task == null) {
            String pickupOtp = generateOtp();
            String deliveryOtp = generateOtp();
            task = new DeliveryTask(allocation, pickupOtp, deliveryOtp);
        } else {
            if (task.getStatus() == DeliveryStatus.DELIVERED) {
                throw new InvalidDeliveryStateException("Cannot reassign a delivery task that is already completed.");
            }
            if (task.getStatus() == DeliveryStatus.PICKED_UP || task.getStatus() == DeliveryStatus.IN_TRANSIT) {
                throw new InvalidDeliveryStateException("Cannot reassign a delivery task that is already in progress.");
            }
            if (task.getStatus() == DeliveryStatus.CANCELLED) {
                throw new InvalidDeliveryStateException("Cannot reassign a cancelled delivery task.");
            }
        }

        task.assignVolunteer(volunteer);
        if (dto.getNotes() != null && !dto.getNotes().trim().isEmpty()) {
            task.setNotes(dto.getNotes().trim());
        }
        task.setIssueReason(null); // clear any previous rejection reason

        DeliveryTask saved = deliveryTaskRepository.save(task);
        log.info("Assigned volunteer ID: {} to delivery task ID: {}", volunteer.getUserId(), saved.getId());
        return DeliveryTaskResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DeliveryTaskResponseDTO acceptAssignment(Long deliveryId, Long volunteerUserId) {
        DeliveryTask task = deliveryTaskRepository.findByIdWithDetails(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery task not found with ID: " + deliveryId));

        if (task.getVolunteer() == null || !task.getVolunteer().getUserId().equals(volunteerUserId)) {
            throw new UnauthorizedRoleException("Only the assigned volunteer can accept this delivery task.");
        }

        if (task.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new InvalidDeliveryStateException("Delivery task cannot be accepted from status: " + task.getStatus());
        }

        task.setStatus(DeliveryStatus.ACCEPTED);
        task.setAcceptedAt(Instant.now());

        DeliveryTask saved = deliveryTaskRepository.save(task);
        log.info("Volunteer ID: {} accepted delivery task ID: {}", volunteerUserId, deliveryId);
        return DeliveryTaskResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DeliveryTaskResponseDTO rejectAssignment(Long deliveryId, Long volunteerUserId, RejectAssignmentDTO dto) {
        DeliveryTask task = deliveryTaskRepository.findByIdWithDetails(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery task not found with ID: " + deliveryId));

        if (task.getVolunteer() == null || !task.getVolunteer().getUserId().equals(volunteerUserId)) {
            throw new UnauthorizedRoleException("Only the assigned volunteer can reject this delivery task.");
        }

        if (task.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new InvalidDeliveryStateException("Delivery task cannot be rejected from status: " + task.getStatus());
        }

        String reason = (dto != null && dto.getReason() != null) ? dto.getReason() : "Declined by volunteer";
        task.setStatus(DeliveryStatus.REJECTED);
        task.setIssueReason(reason);
        task.setVolunteer(null);

        DeliveryTask saved = deliveryTaskRepository.save(task);
        log.info("Volunteer ID: {} rejected delivery task ID: {}. Task returned to reassignment state.", volunteerUserId, deliveryId);
        return DeliveryTaskResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DeliveryTaskResponseDTO confirmPickup(Long deliveryId, Long volunteerUserId, PickupDeliveryDTO dto) {
        DeliveryTask task = deliveryTaskRepository.findByIdWithDetails(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery task not found with ID: " + deliveryId));

        if (task.getVolunteer() == null || !task.getVolunteer().getUserId().equals(volunteerUserId)) {
            throw new UnauthorizedRoleException("Only the assigned volunteer can confirm pickup.");
        }

        // Test rule 12: Pickup before acceptance is rejected
        if (task.getStatus() == DeliveryStatus.ASSIGNED) {
            throw new InvalidDeliveryStateException("Pickup cannot occur before the volunteer has accepted the assignment.");
        }

        if (task.getStatus() == DeliveryStatus.DELIVERED) {
            throw new InvalidDeliveryStateException("Cannot confirm pickup for an already delivered task.");
        }

        if (task.getStatus() == DeliveryStatus.CANCELLED || task.getStatus() == DeliveryStatus.FAILED) {
            throw new InvalidDeliveryStateException("Cannot confirm pickup for a cancelled or failed delivery task.");
        }

        if (task.getStatus() != DeliveryStatus.ACCEPTED) {
            throw new InvalidDeliveryStateException("Invalid status for pickup confirmation: " + task.getStatus());
        }

        // Validate OTP if provided
        if (dto != null && dto.getOtp() != null && !dto.getOtp().trim().isEmpty()) {
            if (!dto.getOtp().trim().equals(task.getPickupOtp())) {
                throw new InvalidOtpException("Invalid pickup verification code. Please request the 6-digit code from the donor kitchen.");
            }
        }

        task.setStatus(DeliveryStatus.PICKED_UP);
        task.setPickedUpAt(Instant.now());
        if (dto != null && dto.getNotes() != null && !dto.getNotes().trim().isEmpty()) {
            task.setNotes((task.getNotes() == null ? "" : task.getNotes() + "\n") + "[Pickup]: " + dto.getNotes().trim());
        }

        // Advance allocation status to IN_TRANSIT
        Allocation allocation = task.getAllocation();
        if (allocation != null) {
            allocation.setStatus(AllocationStatus.IN_TRANSIT);
            allocationRepository.save(allocation);
        }

        DeliveryTask saved = deliveryTaskRepository.save(task);
        log.info("Pickup confirmed for delivery task ID: {} by volunteer ID: {}", deliveryId, volunteerUserId);
        return DeliveryTaskResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DeliveryTaskResponseDTO startTransit(Long deliveryId, Long volunteerUserId, StartTransitDTO dto) {
        DeliveryTask task = deliveryTaskRepository.findByIdWithDetails(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery task not found with ID: " + deliveryId));

        if (task.getVolunteer() == null || !task.getVolunteer().getUserId().equals(volunteerUserId)) {
            throw new UnauthorizedRoleException("Only the assigned volunteer can update transit status.");
        }

        if (task.getStatus() != DeliveryStatus.PICKED_UP) {
            throw new InvalidDeliveryStateException("Transit cannot be started before food has been picked up (current status: " + task.getStatus() + ").");
        }

        task.setStatus(DeliveryStatus.IN_TRANSIT);
        task.setInTransitAt(Instant.now());
        if (dto != null && dto.getNotes() != null && !dto.getNotes().trim().isEmpty()) {
            task.setNotes((task.getNotes() == null ? "" : task.getNotes() + "\n") + "[Transit]: " + dto.getNotes().trim());
        }

        DeliveryTask saved = deliveryTaskRepository.save(task);
        log.info("Transit started for delivery task ID: {} by volunteer ID: {}", deliveryId, volunteerUserId);
        return DeliveryTaskResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DeliveryTaskResponseDTO completeDelivery(Long deliveryId, Long volunteerUserId, CompleteDeliveryDTO dto) {
        DeliveryTask task = deliveryTaskRepository.findByIdWithDetails(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery task not found with ID: " + deliveryId));

        if (task.getVolunteer() == null || !task.getVolunteer().getUserId().equals(volunteerUserId)) {
            throw new UnauthorizedRoleException("Only the assigned volunteer can complete delivery.");
        }

        // Test rule 15: Delivery cannot complete before pickup
        if (task.getStatus() == DeliveryStatus.UNASSIGNED || task.getStatus() == DeliveryStatus.ASSIGNED
                || task.getStatus() == DeliveryStatus.ACCEPTED || task.getStatus() == DeliveryStatus.PENDING_ASSIGNMENT) {
            throw new InvalidDeliveryStateException("Delivery cannot be completed before food pickup has been confirmed.");
        }

        // Test rule 18: Completed delivery cannot be reopened
        if (task.getStatus() == DeliveryStatus.DELIVERED) {
            throw new InvalidDeliveryStateException("Completed delivery cannot be reopened.");
        }

        // Test rule 19: Cancelled or failed tasks cannot be completed
        if (task.getStatus() == DeliveryStatus.CANCELLED || task.getStatus() == DeliveryStatus.FAILED) {
            throw new InvalidDeliveryStateException("Cancelled or failed delivery tasks cannot be completed.");
        }

        if (task.getStatus() != DeliveryStatus.IN_TRANSIT && task.getStatus() != DeliveryStatus.PICKED_UP) {
            throw new InvalidDeliveryStateException("Delivery cannot be completed from status: " + task.getStatus());
        }

        // Validate OTP if provided
        if (dto != null && dto.getOtp() != null && !dto.getOtp().trim().isEmpty()) {
            if (!dto.getOtp().trim().equals(task.getDeliveryOtp())) {
                throw new InvalidOtpException("Invalid delivery confirmation code. Please obtain the 6-digit verification code from the recipient NGO.");
            }
        }

        task.setStatus(DeliveryStatus.DELIVERED);
        task.setDeliveredAt(Instant.now());
        if (dto != null) {
            if (dto.getRecipientName() != null && !dto.getRecipientName().trim().isEmpty()) {
                task.setRecipientName(dto.getRecipientName().trim());
            }
            if (dto.getRecipientPhone() != null && !dto.getRecipientPhone().trim().isEmpty()) {
                task.setRecipientPhone(dto.getRecipientPhone().trim());
            }
            if (dto.getNotes() != null && !dto.getNotes().trim().isEmpty()) {
                task.setNotes((task.getNotes() == null ? "" : task.getNotes() + "\n") + "[Delivered]: " + dto.getNotes().trim());
            }
        }

        // Update related allocation to DELIVERED
        Allocation allocation = task.getAllocation();
        if (allocation != null) {
            allocation.setStatus(AllocationStatus.DELIVERED);
            allocationRepository.save(allocation);

            // Check if entire donation batch has been fulfilled and completed
            FoodDonation donation = allocation.getDonation();
            if (donation != null && donation.getAvailableQuantity().compareTo(BigDecimal.ZERO) == 0) {
                List<Allocation> allAllocations = allocationRepository.findByDonationId(donation.getId());
                boolean allDelivered = allAllocations.stream()
                        .allMatch(a -> a.getStatus() == AllocationStatus.DELIVERED);
                if (allDelivered && donation.getStatus() != DonationStatus.COMPLETED) {
                    donation.setStatus(DonationStatus.COMPLETED);
                    foodDonationRepository.save(donation);
                    log.info("All allocations delivered. Marked food donation batch ID: {} as COMPLETED", donation.getId());
                }
            }
        }

        // Increment volunteer's total delivery count
        VolunteerProfile volunteer = task.getVolunteer();
        if (volunteer != null) {
            volunteer.setTotalDeliveries((volunteer.getTotalDeliveries() != null ? volunteer.getTotalDeliveries() : 0) + 1);
            volunteerProfileRepository.save(volunteer);
        }

        DeliveryTask saved = deliveryTaskRepository.save(task);
        log.info("Delivery task ID: {} successfully completed by volunteer ID: {}", deliveryId, volunteerUserId);
        return DeliveryTaskResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public DeliveryTaskResponseDTO reportIssue(Long deliveryId, Long volunteerUserId, ReportIssueDTO dto) {
        DeliveryTask task = deliveryTaskRepository.findByIdWithDetails(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery task not found with ID: " + deliveryId));

        if (task.getVolunteer() != null && !task.getVolunteer().getUserId().equals(volunteerUserId)) {
            BaseUser actor = userRepository.findById(volunteerUserId).orElse(null);
            if (actor == null || actor.getRole() != UserRole.ADMIN) {
                throw new UnauthorizedRoleException("Only the assigned volunteer or administrator can report an issue for this delivery task.");
            }
        }

        if (task.getStatus() == DeliveryStatus.DELIVERED) {
            throw new InvalidDeliveryStateException("Cannot report delivery issue for an already completed delivery.");
        }

        task.setStatus(DeliveryStatus.FAILED);
        task.setIssueReason(dto.getReason());
        if (dto.getNotes() != null && !dto.getNotes().trim().isEmpty()) {
            task.setNotes((task.getNotes() == null ? "" : task.getNotes() + "\n") + "[Issue]: " + dto.getNotes().trim());
        }

        DeliveryTask saved = deliveryTaskRepository.save(task);
        log.warn("Delivery task ID: {} marked FAILED due to issue: {}", deliveryId, dto.getReason());
        return DeliveryTaskResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryTaskResponseDTO getDeliveryById(Long deliveryId, Long actorUserId) {
        DeliveryTask task = deliveryTaskRepository.findByIdWithDetails(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery task not found with ID: " + deliveryId));

        BaseUser actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + actorUserId));

        // Test rule 7: Volunteer sees only their own tasks
        if (actor.getRole() == UserRole.VOLUNTEER) {
            if (task.getVolunteer() == null || !task.getVolunteer().getUserId().equals(actorUserId)) {
                throw new UnauthorizedRoleException("Access denied: You can only view delivery tasks assigned to you.");
            }
        } else if (actor.getRole() == UserRole.DONOR) {
            if (!task.getAllocation().getDonation().getDonor().getUserId().equals(actorUserId)) {
                throw new UnauthorizedRoleException("Access denied: You can only view deliveries associated with your donations.");
            }
        } else if (actor.getRole() == UserRole.NGO) {
            if (!task.getAllocation().getRequest().getNgo().getUserId().equals(actorUserId)) {
                throw new UnauthorizedRoleException("Access denied: You can only view deliveries associated with your requests.");
            }
        }

        return DeliveryTaskResponseDTO.fromEntity(task);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryTaskResponseDTO getDeliveryByAllocationId(Long allocationId, Long actorUserId) {
        DeliveryTask task = deliveryTaskRepository.findByAllocationId(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery task not found for allocation ID: " + allocationId));

        return getDeliveryById(task.getId(), actorUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryTaskResponseDTO> getMyDeliveries(Long actorUserId) {
        BaseUser actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + actorUserId));

        List<DeliveryTask> tasks;
        if (actor.getRole() == UserRole.VOLUNTEER) {
            tasks = deliveryTaskRepository.findByVolunteerUserId(actorUserId);
        } else if (actor.getRole() == UserRole.DONOR) {
            tasks = deliveryTaskRepository.findByDonorUserId(actorUserId);
        } else if (actor.getRole() == UserRole.NGO) {
            tasks = deliveryTaskRepository.findByNgoUserId(actorUserId);
        } else {
            tasks = deliveryTaskRepository.findAll();
        }

        return tasks.stream().map(DeliveryTaskResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryTaskResponseDTO> getAvailableTasks() {
        return deliveryTaskRepository.findAvailableForAssignment().stream()
                .map(DeliveryTaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VolunteerSummaryDTO> getAvailableVolunteers() {
        return volunteerProfileRepository.findAllActive().stream()
                .map(VolunteerSummaryDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
