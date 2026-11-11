package com.replate.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignVolunteerDTO {

    @NotNull(message = "Allocation ID is mandatory.")
    private Long allocationId;

    @NotNull(message = "Volunteer user ID is mandatory.")
    private Long volunteerId;

    private String notes;

    public AssignVolunteerDTO() {}

    public AssignVolunteerDTO(Long allocationId, Long volunteerId, String notes) {
        this.allocationId = allocationId;
        this.volunteerId = volunteerId;
        this.notes = notes;
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
