package com.replate.domain.entity;

import com.replate.domain.contract.Trackable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Demonstrates:
 * 1. Interface implementation (Trackable) enabling polymorphic tracking alongside FoodDonation.
 * 2. Encapsulation of chain-of-custody OTP validations.
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

    @NotBlank
    @Column(name = "status", nullable = false, length = 30)
    private String status = "UNASSIGNED";

    @NotBlank
    @Column(name = "pickup_otp", nullable = false, length = 6)
    private String pickupOtp;

    @NotBlank
    @Column(name = "delivery_otp", nullable = false, length = 6)
    private String deliveryOtp;

    @Column(name = "picked_up_at")
    private Instant pickedUpAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public DeliveryTask() {
        super();
    }

    public DeliveryTask(Allocation allocation, String pickupOtp, String deliveryOtp) {
        super();
        this.allocation = allocation;
        this.pickupOtp = pickupOtp;
        this.deliveryOtp = deliveryOtp;
        this.status = "UNASSIGNED";
    }

    // Business Methods (Encapsulation)
    public boolean verifyPickup(String enteredOtp) {
        if (this.pickupOtp.equals(enteredOtp) && "ASSIGNED".equalsIgnoreCase(this.status)) {
            this.status = "PICKED_UP";
            this.pickedUpAt = Instant.now();
            return true;
        }
        return false;
    }

    public boolean verifyDelivery(String enteredOtp) {
        if (this.deliveryOtp.equals(enteredOtp) && "PICKED_UP".equalsIgnoreCase(this.status)) {
            this.status = "DELIVERED";
            this.deliveredAt = Instant.now();
            return true;
        }
        return false;
    }

    // Trackable Implementation (Polymorphism)
    @Override
    public String getCurrentStatusDescription() {
        return switch (this.status) {
            case "UNASSIGNED" -> "Awaiting volunteer assignment in the cluster";
            case "ASSIGNED" -> "Volunteer en route to donor kitchen for pickup";
            case "PICKED_UP" -> "Food picked up; in transit to NGO beneficiary site";
            case "DELIVERED" -> "Successfully delivered and confirmed by NGO";
            case "FAILED" -> "Delivery could not be completed";
            default -> "Status: " + this.status;
        };
    }

    @Override
    public int getProgressPercentage() {
        return switch (this.status) {
            case "UNASSIGNED" -> 10;
            case "ASSIGNED" -> 35;
            case "PICKED_UP" -> 70;
            case "DELIVERED" -> 100;
            default -> 0;
        };
    }

    @Override
    public boolean isTerminal() {
        return "DELIVERED".equalsIgnoreCase(this.status) || "FAILED".equalsIgnoreCase(this.status);
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
        if (volunteer != null && "UNASSIGNED".equalsIgnoreCase(this.status)) {
            this.status = "ASSIGNED";
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public Instant getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(Instant pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
