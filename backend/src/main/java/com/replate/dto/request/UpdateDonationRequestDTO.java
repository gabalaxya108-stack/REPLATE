package com.replate.dto.request;

import com.replate.domain.enums.DietaryType;
import com.replate.domain.enums.FoodCategory;
import com.replate.domain.enums.QuantityUnit;
import com.replate.domain.enums.StorageCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public class UpdateDonationRequestDTO {

    @NotBlank(message = "Food donation title is required")
    @Size(max = 160, message = "Title must not exceed 160 characters")
    private String title;

    @NotNull(message = "Food category is required")
    private FoodCategory category;

    @NotNull(message = "Total quantity is required")
    @Positive(message = "Total quantity must be strictly greater than zero")
    private BigDecimal totalQuantity;

    @NotNull(message = "Quantity unit is required")
    private QuantityUnit quantityUnit;

    @NotNull(message = "Dietary type is required")
    private DietaryType dietaryType;

    @NotNull(message = "Storage condition is required")
    private StorageCondition storageCondition;

    @NotNull(message = "Preparation time is required")
    private Instant preparationTime;

    @NotNull(message = "Expiry time is required")
    private Instant expiryTime;

    @Size(max = 1000, message = "Pickup notes must not exceed 1000 characters")
    private String pickupNotes;

    public UpdateDonationRequestDTO() {}

    public UpdateDonationRequestDTO(String title, FoodCategory category, BigDecimal totalQuantity,
                                    QuantityUnit quantityUnit, DietaryType dietaryType,
                                    StorageCondition storageCondition, Instant preparationTime,
                                    Instant expiryTime, String pickupNotes) {
        this.title = title;
        this.category = category;
        this.totalQuantity = totalQuantity;
        this.quantityUnit = quantityUnit;
        this.dietaryType = dietaryType;
        this.storageCondition = storageCondition;
        this.preparationTime = preparationTime;
        this.expiryTime = expiryTime;
        this.pickupNotes = pickupNotes;
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
}
