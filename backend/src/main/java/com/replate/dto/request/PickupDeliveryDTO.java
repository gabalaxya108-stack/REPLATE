package com.replate.dto.request;

public class PickupDeliveryDTO {

    private String otp;
    private String notes;

    public PickupDeliveryDTO() {}

    public PickupDeliveryDTO(String otp, String notes) {
        this.otp = otp;
        this.notes = notes;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
