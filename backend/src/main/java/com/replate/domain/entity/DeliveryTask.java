package com.replate.domain.entity;

import com.replate.domain.contract.Trackable;
import com.replate.domain.enums.DeliveryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Demonstrates:
 * 1. Interface implementation (Trackable) enabling polymorphic tracking alongside FoodDonation.
 * 2. Encapsulation of chain-of-custody OTP validations and delivery state transitions.
 * 3. Bidirectional relational integrity with Allocation and VolunteerProfile.
 */
@Entity
@Table(name = "delivery_task")
public class DeliveryTask extends BaseEntity implements Trackable {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocation_id", nullable = false, unique = true)
    private Allocation allocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id")
    private VolunteerProfile volunteer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DeliveryStatus status = DeliveryStatus.UNASSIGNED;

    @NotBlank
    @Column(name = "pickup_otp", nullable = false, length = 6)
    private String pickupOtp;

    @NotBlank
    @Column(name = "delivery_otp", nullable = false, length = 6)
    private String deliveryOtp;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "picked_up_at")
    private Instant pickedUpAt;

    @Column(name = "in_transit_at")
    private Instant inTransitAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "recipient_name", length = 120)
    private String recipientName;

    @Column(name = "recipient_phone", length = 20)
    private String recipientPhone;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "issue_reason", columnDefinition = "TEXT")
    private String issueReason;

    public DeliveryTask() {
        super();
    }

    public DeliveryTask(Allocation allocation, String pickupOtp, String deliveryOtp) {
        super();
        this.allocation = allocation;
        this.pickupOtp = pickupOtp;
        this.deliveryOtp = deliveryOtp;
        this.status = DeliveryStatus.UNASSIGNED;
    }

    // Business Methods (Encapsulation)

    /**
     * Assigns a volunteer to this delivery task.
     */
    public void assignVolunteer(VolunteerProfile volunteer) {
        this.volunteer = volunteer;
        this.status = DeliveryStatus.ASSIGNED;
        this.assignedAt = Instant.now();
    }

    /**
     * Volunteer accepts assignment.
     */
    public boolean acceptAssignment(Long volunteerUserId) {
        if (this.volunteer != null && this.volunteer.getUserId().equals(volunteerUserId)
                && this.status == DeliveryStatus.ASSIGNED) {
            this.status = DeliveryStatus.ACCEPTED;
            this.acceptedAt = Instant.now();
            return true;
        }
        return false;
    }

    /**
     * Volunteer rejects assignment, releasing it for reassignment.
     */
    public boolean rejectAssignment(Long volunteerUserId, String reason) {
        if (this.volunteer != null && this.volunteer.getUserId().equals(volunteerUserId)
                && this.status == DeliveryStatus.ASSIGNED) {
            this.status = DeliveryStatus.REJECTED;
            this.issueReason = reason;
            this.volunteer = null;
            return true;
        }
        return false;
    }

    /**
     * Confirms pickup with optional OTP check and notes.
     */
    public boolean verifyPickup(String enteredOtp, String pickupNotes) {
        if (this.status != DeliveryStatus.ACCEPTED && this.status != DeliveryStatus.ASSIGNED) {
            return false;
        }
        if (enteredOtp != null && !enteredOtp.trim().isEmpty() && !enteredOtp.trim().equals(this.pickupOtp)) {
            return false;
        }
        this.status = DeliveryStatus.PICKED_UP;
        this.pickedUpAt = Instant.now();
        if (pickupNotes != null && !pickupNotes.trim().isEmpty()) {
            this.notes = (this.notes == null) ? pickupNotes : this.notes + "\n[Pickup]: " + pickupNotes;
        }
        return true;
    }

    /**
     * Transitions task to in-transit status.
     */
    public boolean startTransit(String transitNotes) {
        if (this.status != DeliveryStatus.PICKED_UP) {
            return false;
        }
        this.status = DeliveryStatus.IN_TRANSIT;
        this.inTransitAt = Instant.now();
        if (transitNotes != null && !transitNotes.trim().isEmpty()) {
            this.notes = (this.notes == null) ? transitNotes : this.notes + "\n[Transit]: " + transitNotes;
        }
        return true;
    }

    /**
     * Confirms final delivery with optional OTP check, recipient details, and notes.
     */
    public boolean verifyDelivery(String enteredOtp, String recipientName, String recipientPhone, String deliveryNotes) {
        if (this.status != DeliveryStatus.IN_TRANSIT && this.status != DeliveryStatus.PICKED_UP) {
            return false;
        }
        if (enteredOtp != null && !enteredOtp.trim().isEmpty() && !enteredOtp.trim().equals(this.deliveryOtp)) {
            return false;
        }
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = Instant.now();
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        if (deliveryNotes != null && !deliveryNotes.trim().isEmpty()) {
            this.notes = (this.notes == null) ? deliveryNotes : this.notes + "\n[Delivery]: " + deliveryNotes;
        }
        return true;
    }

    /**
     * Reports an issue or marks the task as failed.
     */
    public void reportIssue(String reason) {
        this.status = DeliveryStatus.FAILED;
        this.issueReason = reason;
    }

    /**
     * Cancels the delivery task.
     */
    public void cancel(String reason) {
        this.status = DeliveryStatus.CANCELLED;
        this.issueReason = reason;
    }

    // Trackable Implementation (Polymorphism)
    @Override
    public String getCurrentStatusDescription() {
        return switch (this.status) {
            case PENDING_ASSIGNMENT, UNASSIGNED -> "Awaiting volunteer assignment in the cluster";
            case ASSIGNED -> "Volunteer assigned; pending acceptance";
            case ACCEPTED -> "Volunteer accepted run; en route to donor kitchen for pickup";
            case PICKED_UP -> "Food verified and picked up; preparing for dispatch";
            case IN_TRANSIT -> "Food package in transit to NGO distribution center";
            case DELIVERED -> "Successfully delivered and confirmed by recipient NGO";
            case REJECTED -> "Assignment rejected by volunteer; awaiting reassignment";
            case CANCELLED -> "Delivery task has been cancelled";
            case FAILED -> "Delivery issue reported: " + (issueReason != null ? issueReason : "delivery failed");
        };
    }

    @Override
    public int getProgressPercentage() {
        return switch (this.status) {
            case PENDING_ASSIGNMENT, UNASSIGNED -> 10;
            case ASSIGNED -> 25;
            case ACCEPTED -> 40;
            case PICKED_UP -> 65;
            case IN_TRANSIT -> 85;
            case DELIVERED -> 100;
            case REJECTED, CANCELLED, FAILED -> 0;
        };
    }

    @Override
    public boolean isTerminal() {
        return this.status == DeliveryStatus.DELIVERED
                || this.status == DeliveryStatus.FAILED
                || this.status == DeliveryStatus.CANCELLED;
    }

    // Getters and Setters
    public Allocation getAllocation() {
        return allocation;
    }

    public void setAllocation(Allocation allocation) {
        this.allocation = allocation;
    }

    public VolunteerProfile getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(VolunteerProfile volunteer) {
        this.volunteer = volunteer;
        if (volunteer != null && (this.status == DeliveryStatus.UNASSIGNED || this.status == DeliveryStatus.PENDING_ASSIGNMENT)) {
            this.status = DeliveryStatus.ASSIGNED;
            this.assignedAt = Instant.now();
        }
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public String getPickupOtp() {
        return pickupOtp;
    }

    public void setPickupOtp(String pickupOtp) {
        this.pickupOtp = pickupOtp;
    }

    public String getDeliveryOtp() {
        return deliveryOtp;
    }

    public void setDeliveryOtp(String deliveryOtp) {
        this.deliveryOtp = deliveryOtp;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Instant getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(Instant pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public Instant getInTransitAt() {
        return inTransitAt;
    }

    public void setInTransitAt(Instant inTransitAt) {
        this.inTransitAt = inTransitAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getIssueReason() {
        return issueReason;
    }

    public void setIssueReason(String issueReason) {
        this.issueReason = issueReason;
    }
}
