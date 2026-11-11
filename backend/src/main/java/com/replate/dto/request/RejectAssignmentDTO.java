package com.replate.dto.request;

public class RejectAssignmentDTO {

    private String reason;

    public RejectAssignmentDTO() {}

    public RejectAssignmentDTO(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
