package com.replate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RejectFoodRequestDTO {

    @NotBlank(message = "Rejection reason / justification is required")
    @Size(max = 255, message = "Rejection reason must not exceed 255 characters")
    private String rejectionReason;

    public RejectFoodRequestDTO() {}

    public RejectFoodRequestDTO(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}

