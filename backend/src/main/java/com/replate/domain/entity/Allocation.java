package com.replate.domain.entity;

import com.replate.domain.enums.AllocationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Represents a formal, inventory-deducted food allocation fulfilling an approved NGO food request.
 * Demonstrates:
 * 1. Association mapping (@ManyToOne to FoodRequest and FoodDonation).
 * 2. Audit traceability via allocatedBy user association.
 * 3. Encapsulated state transition and quantity immutability.
 */
@Entity
@Table(name = "allocation")
public class Allocation extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private FoodRequest request;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private FoodDonation donation;

    @NotNull
    @Positive
    @Column(name = "allocated_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal allocatedQuantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AllocationStatus status = AllocationStatus.AWAITING_PICKUP;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocated_by")
    private BaseUser allocatedBy;

    public Allocation() {
        super();
    }

    public Allocation(FoodRequest request, FoodDonation donation, BigDecimal allocatedQuantity) {
        super();
        this.request = request;
        this.donation = donation;
        this.allocatedQuantity = allocatedQuantity;
        this.status = AllocationStatus.AWAITING_PICKUP;
    }

    public Allocation(FoodRequest request, FoodDonation donation, BigDecimal allocatedQuantity, BaseUser allocatedBy) {
        super();
        this.request = request;
        this.donation = donation;
        this.allocatedQuantity = allocatedQuantity;
        this.allocatedBy = allocatedBy;
        this.status = AllocationStatus.AWAITING_PICKUP;
    }

    public FoodRequest getRequest() {
        return request;
    }

    public void setRequest(FoodRequest request) {
        this.request = request;
    }

    public FoodDonation getDonation() {
        return donation;
    }

    public void setDonation(FoodDonation donation) {
        this.donation = donation;
    }

    public BigDecimal getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(BigDecimal allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public AllocationStatus getStatus() {
        return status;
    }

    public void setStatus(AllocationStatus status) {
        this.status = status;
    }

    public BaseUser getAllocatedBy() {
        return allocatedBy;
    }

    public void setAllocatedBy(BaseUser allocatedBy) {
        this.allocatedBy = allocatedBy;
    }
}
