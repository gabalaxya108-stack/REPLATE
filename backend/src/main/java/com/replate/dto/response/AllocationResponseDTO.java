package com.replate.dto.response;

import com.replate.domain.entity.Allocation;
import com.replate.domain.enums.AllocationStatus;
import com.replate.domain.enums.DietaryType;
import com.replate.domain.enums.FoodCategory;
import com.replate.domain.enums.QuantityUnit;
import com.replate.domain.enums.StorageCondition;

import java.math.BigDecimal;
import java.time.Instant;

public class AllocationResponseDTO {

    private Long id;
    private Long requestId;
    private Long donationId;
    private String donationTitle;
    private FoodCategory donationCategory;
    private QuantityUnit quantityUnit;
    private DietaryType dietaryType;
    private StorageCondition storageCondition;
    private BigDecimal allocatedQuantity;
    private AllocationStatus status;

    private Long donorId;
    private String donorName;
    private String donorCity;
    private String donorPhone;

    private Long ngoId;
    private String ngoName;
    private String ngoDarpanId;
    private String ngoPhone;

    private Long allocatedById;
    private String allocatedByName;

    private BigDecimal requestRequestedQuantity;
    private String requestIntendedBeneficiaries;
    private BigDecimal donationAvailableQuantity;
    private BigDecimal donationTotalQuantity;

    private Instant createdAt;
    private Instant updatedAt;

    public AllocationResponseDTO() {}

    public static AllocationResponseDTO fromEntity(Allocation allocation) {
        AllocationResponseDTO dto = new AllocationResponseDTO();
        dto.setId(allocation.getId());
        dto.setAllocatedQuantity(allocation.getAllocatedQuantity());
        dto.setStatus(allocation.getStatus());
        dto.setCreatedAt(allocation.getCreatedAt());
        dto.setUpdatedAt(allocation.getUpdatedAt());

        if (allocation.getDonation() != null) {
            dto.setDonationId(allocation.getDonation().getId());
            dto.setDonationTitle(allocation.getDonation().getTitle());
            dto.setDonationCategory(allocation.getDonation().getCategory());
            dto.setQuantityUnit(allocation.getDonation().getQuantityUnit());
            dto.setDietaryType(allocation.getDonation().getDietaryType());
            dto.setStorageCondition(allocation.getDonation().getStorageCondition());
            dto.setDonationAvailableQuantity(allocation.getDonation().getAvailableQuantity());
            dto.setDonationTotalQuantity(allocation.getDonation().getTotalQuantity());

            if (allocation.getDonation().getDonor() != null) {
                dto.setDonorId(allocation.getDonation().getDonor().getUserId());
                dto.setDonorName(allocation.getDonation().getDonor().getOrganizationName());
                if (allocation.getDonation().getDonor().getUser() != null) {
                    dto.setDonorPhone(allocation.getDonation().getDonor().getUser().getPhoneNumber());
                    if (allocation.getDonation().getDonor().getUser().getLocation() != null) {
                        dto.setDonorCity(allocation.getDonation().getDonor().getUser().getLocation().getCity());
                    }
                }
            }
        }

        if (allocation.getRequest() != null) {
            dto.setRequestId(allocation.getRequest().getId());
            dto.setRequestRequestedQuantity(allocation.getRequest().getRequestedQuantity());
            dto.setRequestIntendedBeneficiaries(allocation.getRequest().getIntendedBeneficiaries());

            if (allocation.getRequest().getNgo() != null) {
                dto.setNgoId(allocation.getRequest().getNgo().getUserId());
                dto.setNgoName(allocation.getRequest().getNgo().getNgoName());
                dto.setNgoDarpanId(allocation.getRequest().getNgo().getDarpanId());
                if (allocation.getRequest().getNgo().getUser() != null) {
                    dto.setNgoPhone(allocation.getRequest().getNgo().getUser().getPhoneNumber());
                }
            }
        }

        if (allocation.getAllocatedBy() != null) {
            dto.setAllocatedById(allocation.getAllocatedBy().getId());
            dto.setAllocatedByName(allocation.getAllocatedBy().getFullName());
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    public String getDonationTitle() {
        return donationTitle;
    }

    public void setDonationTitle(String donationTitle) {
        this.donationTitle = donationTitle;
    }

    public FoodCategory getDonationCategory() {
        return donationCategory;
    }

    public void setDonationCategory(FoodCategory donationCategory) {
        this.donationCategory = donationCategory;
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

    public String getDonorPhone() {
        return donorPhone;
    }

    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
    }

    public Long getNgoId() {
        return ngoId;
    }

    public void setNgoId(Long ngoId) {
        this.ngoId = ngoId;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }

    public String getNgoDarpanId() {
        return ngoDarpanId;
    }

    public void setNgoDarpanId(String ngoDarpanId) {
        this.ngoDarpanId = ngoDarpanId;
    }

    public String getNgoPhone() {
        return ngoPhone;
    }

    public void setNgoPhone(String ngoPhone) {
        this.ngoPhone = ngoPhone;
    }

    public Long getAllocatedById() {
        return allocatedById;
    }

    public void setAllocatedById(Long allocatedById) {
        this.allocatedById = allocatedById;
    }

    public String getAllocatedByName() {
        return allocatedByName;
    }

    public void setAllocatedByName(String allocatedByName) {
        this.allocatedByName = allocatedByName;
    }

    public BigDecimal getRequestRequestedQuantity() {
        return requestRequestedQuantity;
    }

    public void setRequestRequestedQuantity(BigDecimal requestRequestedQuantity) {
        this.requestRequestedQuantity = requestRequestedQuantity;
    }

    public String getRequestIntendedBeneficiaries() {
        return requestIntendedBeneficiaries;
    }

    public void setRequestIntendedBeneficiaries(String requestIntendedBeneficiaries) {
        this.requestIntendedBeneficiaries = requestIntendedBeneficiaries;
    }

    public BigDecimal getDonationAvailableQuantity() {
        return donationAvailableQuantity;
    }

    public void setDonationAvailableQuantity(BigDecimal donationAvailableQuantity) {
        this.donationAvailableQuantity = donationAvailableQuantity;
    }

    public BigDecimal getDonationTotalQuantity() {
        return donationTotalQuantity;
    }

    public void setDonationTotalQuantity(BigDecimal donationTotalQuantity) {
        this.donationTotalQuantity = donationTotalQuantity;
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

