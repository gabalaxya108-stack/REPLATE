package com.replate.dto.request;

public class StartTransitDTO {

    private String notes;

    public StartTransitDTO() {}

    public StartTransitDTO(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
