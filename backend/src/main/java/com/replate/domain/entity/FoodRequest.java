package com.replate.domain.entity;

import com.replate.domain.contract.Approvable;
import com.replate.domain.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Demonstrates:
 * 1. Interface Implementation (Approvable).
 * 2. Encapsulated state machine transitions.
 * 3. Relational association between NgoProfile and FoodDonation.
 */
@Entity
@Table(name = "food_request")
public class FoodRequest extends BaseEntity implements Approvable {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private FoodDonation donation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id", nullable = false)
    private NgoProfile ngo;

    @NotNull
    @Positive
    @Column(name = "requested_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal requestedQuantity;

    @NotBlank
    @Column(name = "intended_beneficiaries", nullable = false, columnDefinition = "TEXT")
    private String intendedBeneficiaries;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    public FoodRequest() {
        super();
    }

    public FoodRequest(FoodDonation donation, NgoProfile ngo, BigDecimal requestedQuantity, String intendedBeneficiaries) {
        super();
        this.donation = donation;
        this.ngo = ngo;
        this.requestedQuantity = requestedQuantity;
        this.intendedBeneficiaries = intendedBeneficiaries;
        this.status = RequestStatus.PENDING;
    }

    // Approvable Interface Contract Implementation
    @Override
    public boolean approve(Long approverUserId, String remarks) {
        if (!isPendingDecision()) {
            return false;
        }
        // Deduct from underlying donation
        boolean deducted = donation.deductQuantity(this.requestedQuantity);
        if (deducted) {
            this.status = RequestStatus.APPROVED;
            this.setUpdatedAt(Instant.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean reject(Long approverUserId, String rejectionReason) {
        if (!isPendingDecision()) {
            return false;
        }
        this.status = RequestStatus.REJECTED;
        this.rejectionReason = rejectionReason;
        this.setUpdatedAt(Instant.now());
        return true;
    }

    @Override
    public boolean isPendingDecision() {
        return this.status == RequestStatus.PENDING;
    }

    // Getters & Setters
    public FoodDonation getDonation() {
        return donation;
    }

    public void setDonation(FoodDonation donation) {
        this.donation = donation;
    }

    public NgoProfile getNgo() {
        return ngo;
    }

    public void setNgo(NgoProfile ngo) {
        this.ngo = ngo;
    }

    public BigDecimal getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(BigDecimal requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getIntendedBeneficiaries() {
        return intendedBeneficiaries;
    }

    public void setIntendedBeneficiaries(String intendedBeneficiaries) {
        this.intendedBeneficiaries = intendedBeneficiaries;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}

