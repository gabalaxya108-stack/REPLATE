package com.replate.domain.entity;

import com.replate.domain.contract.Trackable;
import com.replate.domain.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Demonstrates:
 * 1. Encapsulation (Domain logic enforcing invariant quantities and lifecycle states).
 * 2. Interface Implementation (Trackable).
 * 3. Composition with LocationCoordinate and Enums.
 */
@Entity
@Table(name = "food_donation")
public class FoodDonation extends BaseEntity implements Trackable {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private DonorProfile donor;

    @NotBlank
    @Column(name = "title", nullable = false, length = 160)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private FoodCategory category;

    @NotNull
    @Positive
    @Column(name = "total_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalQuantity;

    @NotNull
    @Column(name = "available_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal availableQuantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_unit", nullable = false, length = 20)
    private QuantityUnit quantityUnit;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "dietary_type", nullable = false, length = 30)
    private DietaryType dietaryType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "storage_condition", nullable = false, length = 40)
    private StorageCondition storageCondition;

    @NotNull
    @Column(name = "preparation_time", nullable = false)
    private Instant preparationTime;

    @NotNull
    @Column(name = "expiry_time", nullable = false)
    private Instant expiryTime;

    @Column(name = "pickup_notes", columnDefinition = "TEXT")
    private String pickupNotes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DonationStatus status = DonationStatus.AVAILABLE;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    // Constructors (Constructor Overloading)
    public FoodDonation() {
        super();
    }

    public FoodDonation(DonorProfile donor, String title, FoodCategory category,
                        BigDecimal totalQuantity, QuantityUnit quantityUnit,
                        DietaryType dietaryType, StorageCondition storageCondition,
                        Instant preparationTime, Instant expiryTime) {
        super();
        this.donor = donor;
        this.title = title;
        this.category = category;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
        this.quantityUnit = quantityUnit;
        this.dietaryType = dietaryType;
        this.storageCondition = storageCondition;
        this.preparationTime = preparationTime;
        this.expiryTime = expiryTime;
        this.status = DonationStatus.AVAILABLE;
    }

    // Business Logic Methods (Encapsulation)
    public synchronized boolean deductQuantity(BigDecimal requestedQty) {
        if (requestedQty == null || requestedQty.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (this.availableQuantity.compareTo(requestedQty) < 0) {
            return false;
        }
        this.availableQuantity = this.availableQuantity.subtract(requestedQty);
        if (this.availableQuantity.compareTo(BigDecimal.ZERO) == 0) {
            this.status = DonationStatus.FULLY_ALLOCATED;
        } else {
            this.status = DonationStatus.PARTIALLY_ALLOCATED;
        }
        return true;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryTime);
    }

    // Implementation of Trackable Interface (Runtime Polymorphism)
    @Override
    public String getCurrentStatusDescription() {
        return switch (this.status) {
            case AVAILABLE -> "Available for NGOs (" + this.availableQuantity + " " + this.quantityUnit.getLabel() + " remaining)";
            case PARTIALLY_ALLOCATED -> "Partially claimed (" + this.availableQuantity + " remaining)";
            case FULLY_ALLOCATED -> "Fully allocated to NGO partners";
            case COMPLETED -> "Successfully collected and distributed";
            case EXPIRED -> "Safe consumption window expired";
            case CANCELLED -> "Donation retracted by donor";
        };
    }

    @Override
    public int getProgressPercentage() {
        if (this.status == DonationStatus.COMPLETED) return 100;
        if (this.status == DonationStatus.FULLY_ALLOCATED) return 75;
        if (this.status == DonationStatus.PARTIALLY_ALLOCATED) return 50;
        if (this.status == DonationStatus.AVAILABLE) return 25;
        return 0;
    }

    @Override
    public boolean isTerminal() {
        return this.status == DonationStatus.COMPLETED ||
               this.status == DonationStatus.EXPIRED ||
               this.status == DonationStatus.CANCELLED;
    }

    // Getters and Setters
    public DonorProfile getDonor() {
        return donor;
    }

    public void setDonor(DonorProfile donor) {
        this.donor = donor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FoodCategory getCategory() {
        return category;
    }

    public void setCategory(FoodCategory category) {
        this.category = category;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public QuantityUnit getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(QuantityUnit quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public DietaryType getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(DietaryType dietaryType) {
        this.dietaryType = dietaryType;
    }

    public StorageCondition getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(StorageCondition storageCondition) {
        this.storageCondition = storageCondition;
    }

    public Instant getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Instant preparationTime) {
        this.preparationTime = preparationTime;
    }

    public Instant getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Instant expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getPickupNotes() {
        return pickupNotes;
    }

    public void setPickupNotes(String pickupNotes) {
        this.pickupNotes = pickupNotes;
    }

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

