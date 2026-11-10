package com.replate.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateAllocationDTO {

    @NotNull(message = "Food request ID is required.")
    private Long requestId;

    @DecimalMin(value = "0.01", message = "Allocated quantity must be strictly greater than zero.")
    private BigDecimal allocatedQuantity;

    private String notes;

    public CreateAllocationDTO() {}

    public CreateAllocationDTO(Long requestId, BigDecimal allocatedQuantity) {
        this.requestId = requestId;
        this.allocatedQuantity = allocatedQuantity;
    }

    public CreateAllocationDTO(Long requestId, BigDecimal allocatedQuantity, String notes) {
        this.requestId = requestId;
        this.allocatedQuantity = allocatedQuantity;
        this.notes = notes;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public BigDecimal getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(BigDecimal allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
