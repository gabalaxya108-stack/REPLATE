package com.replate.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ReportIssueDTO {

    @NotBlank(message = "Reason for delivery issue is mandatory.")
    private String reason;

    private String notes;

    public ReportIssueDTO() {}

    public ReportIssueDTO(String reason, String notes) {
        this.reason = reason;
        this.notes = notes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
