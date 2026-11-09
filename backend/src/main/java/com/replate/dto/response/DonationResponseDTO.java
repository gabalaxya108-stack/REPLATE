package com.replate.dto.response;

import com.replate.domain.entity.FoodDonation;
import com.replate.domain.enums.DietaryType;
import com.replate.domain.enums.DonationStatus;
import com.replate.domain.enums.FoodCategory;
import com.replate.domain.enums.QuantityUnit;
import com.replate.domain.enums.StorageCondition;

import java.math.BigDecimal;
import java.time.Instant;

public class DonationResponseDTO {

    private Long id;
    private Long donorId;
    private String donorName;
    private String donorCity;
    private String donorAddress;
    private String donorPhone;
    private String title;
    private FoodCategory category;
    private BigDecimal totalQuantity;
    private BigDecimal availableQuantity;
    private QuantityUnit quantityUnit;
    private DietaryType dietaryType;
    private StorageCondition storageCondition;
    private Instant preparationTime;
    private Instant expiryTime;
    private String pickupNotes;
    private DonationStatus status;
    private String statusDescription;
    private int progressPercentage;
    private boolean terminal;
    private boolean expired;
    private Instant createdAt;
    private Instant updatedAt;

    public DonationResponseDTO() {}

    public static DonationResponseDTO fromEntity(FoodDonation donation) {
        DonationResponseDTO dto = new DonationResponseDTO();
        dto.setId(donation.getId());
        if (donation.getDonor() != null) {
            dto.setDonorId(donation.getDonor().getUserId());
            dto.setDonorName(donation.getDonor().getOrganizationName());
            if (donation.getDonor().getUser() != null) {
                dto.setDonorPhone(donation.getDonor().getUser().getPhoneNumber());
                if (donation.getDonor().getUser().getLocation() != null) {
                    dto.setDonorCity(donation.getDonor().getUser().getLocation().getCity());
                    dto.setDonorAddress(donation.getDonor().getUser().getLocation().getAddressLine1());
                }
            }
        }
        dto.setTitle(donation.getTitle());
        dto.setCategory(donation.getCategory());
        dto.setTotalQuantity(donation.getTotalQuantity());
        dto.setAvailableQuantity(donation.getAvailableQuantity());
        dto.setQuantityUnit(donation.getQuantityUnit());
        dto.setDietaryType(donation.getDietaryType());
        dto.setStorageCondition(donation.getStorageCondition());
        dto.setPreparationTime(donation.getPreparationTime());
        dto.setExpiryTime(donation.getExpiryTime());
        dto.setPickupNotes(donation.getPickupNotes());
        dto.setStatus(donation.getStatus());
        dto.setStatusDescription(donation.getCurrentStatusDescription());
        dto.setProgressPercentage(donation.getProgressPercentage());
        dto.setTerminal(donation.isTerminal());
        dto.setExpired(donation.isExpired());
        dto.setCreatedAt(donation.getCreatedAt());
        dto.setUpdatedAt(donation.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorCity() {
        return donorCity;
    }

    public void setDonorCity(String donorCity) {
        this.donorCity = donorCity;
    }

    public String getDonorAddress() {
        return donorAddress;
    }

    public void setDonorAddress(String donorAddress) {
        this.donorAddress = donorAddress;
    }

    public String getDonorPhone() {
        return donorPhone;
    }

    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
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

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
