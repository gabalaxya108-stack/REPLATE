package com.replate.dto.response;

import com.replate.domain.entity.FoodRequest;
import com.replate.domain.enums.*;

import java.math.BigDecimal;
import java.time.Instant;

public class FoodRequestResponseDTO {

    private Long id;
    private Long donationId;
    private String donationTitle;
    private FoodCategory donationCategory;
    private BigDecimal donationTotalQuantity;
    private BigDecimal donationAvailableQuantity;
    private QuantityUnit donationQuantityUnit;
    private DietaryType donationDietaryType;
    private StorageCondition donationStorageCondition;
    private Instant donationExpiryTime;

    private Long donorId;
    private String donorName;
    private String donorCity;
    private String donorPhone;

    private Long ngoId;
    private String ngoName;
    private String ngoDarpanId;
    private String ngoPhone;

    private BigDecimal requestedQuantity;
    private String intendedBeneficiaries;
    private RequestStatus status;
    private String rejectionReason;
    private Instant createdAt;
    private Instant updatedAt;

    public FoodRequestResponseDTO() {}

    public static FoodRequestResponseDTO fromEntity(FoodRequest request) {
        FoodRequestResponseDTO dto = new FoodRequestResponseDTO();
        dto.setId(request.getId());

        if (request.getDonation() != null) {
            dto.setDonationId(request.getDonation().getId());
            dto.setDonationTitle(request.getDonation().getTitle());
            dto.setDonationCategory(request.getDonation().getCategory());
            dto.setDonationTotalQuantity(request.getDonation().getTotalQuantity());
            dto.setDonationAvailableQuantity(request.getDonation().getAvailableQuantity());
            dto.setDonationQuantityUnit(request.getDonation().getQuantityUnit());
            dto.setDonationDietaryType(request.getDonation().getDietaryType());
            dto.setDonationStorageCondition(request.getDonation().getStorageCondition());
            dto.setDonationExpiryTime(request.getDonation().getExpiryTime());

            if (request.getDonation().getDonor() != null) {
                dto.setDonorId(request.getDonation().getDonor().getUserId());
                dto.setDonorName(request.getDonation().getDonor().getOrganizationName());
                if (request.getDonation().getDonor().getUser() != null) {
                    dto.setDonorPhone(request.getDonation().getDonor().getUser().getPhoneNumber());
                    if (request.getDonation().getDonor().getUser().getLocation() != null) {
                        dto.setDonorCity(request.getDonation().getDonor().getUser().getLocation().getCity());
                    }
                }
            }
        }

        if (request.getNgo() != null) {
            dto.setNgoId(request.getNgo().getUserId());
            dto.setNgoName(request.getNgo().getNgoName());
            dto.setNgoDarpanId(request.getNgo().getDarpanId());
            if (request.getNgo().getUser() != null) {
                dto.setNgoPhone(request.getNgo().getUser().getPhoneNumber());
            }
        }

        dto.setRequestedQuantity(request.getRequestedQuantity());
        dto.setIntendedBeneficiaries(request.getIntendedBeneficiaries());
        dto.setStatus(request.getStatus());
        dto.setRejectionReason(request.getRejectionReason());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getDonationTotalQuantity() {
        return donationTotalQuantity;
    }

    public void setDonationTotalQuantity(BigDecimal donationTotalQuantity) {
        this.donationTotalQuantity = donationTotalQuantity;
    }

    public BigDecimal getDonationAvailableQuantity() {
        return donationAvailableQuantity;
    }

    public void setDonationAvailableQuantity(BigDecimal donationAvailableQuantity) {
        this.donationAvailableQuantity = donationAvailableQuantity;
    }

    public QuantityUnit getDonationQuantityUnit() {
        return donationQuantityUnit;
    }

    public void setDonationQuantityUnit(QuantityUnit donationQuantityUnit) {
        this.donationQuantityUnit = donationQuantityUnit;
    }

    public DietaryType getDonationDietaryType() {
        return donationDietaryType;
    }

    public void setDonationDietaryType(DietaryType donationDietaryType) {
        this.donationDietaryType = donationDietaryType;
    }

    public StorageCondition getDonationStorageCondition() {
        return donationStorageCondition;
    }

    public void setDonationStorageCondition(StorageCondition donationStorageCondition) {
        this.donationStorageCondition = donationStorageCondition;
    }

    public Instant getDonationExpiryTime() {
        return donationExpiryTime;
    }

    public void setDonationExpiryTime(Instant donationExpiryTime) {
        this.donationExpiryTime = donationExpiryTime;
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
