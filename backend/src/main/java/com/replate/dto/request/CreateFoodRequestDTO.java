package com.replate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateFoodRequestDTO {

    @NotNull(message = "Donation ID is required")
    private Long donationId;

    @NotNull(message = "Requested quantity is required")
    @Positive(message = "Requested quantity must be strictly greater than zero")
    private BigDecimal requestedQuantity;

    @NotBlank(message = "Intended beneficiaries explanation is required")
    @Size(max = 1000, message = "Beneficiaries description must not exceed 1000 characters")
    private String intendedBeneficiaries;

    public CreateFoodRequestDTO() {}

    public CreateFoodRequestDTO(Long donationId, BigDecimal requestedQuantity, String intendedBeneficiaries) {
        this.donationId = donationId;
        this.requestedQuantity = requestedQuantity;
        this.intendedBeneficiaries = intendedBeneficiaries;
    }

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
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
}

