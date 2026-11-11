package com.replate.dto.request;

public class CompleteDeliveryDTO {

    private String otp;
    private String recipientName;
    private String recipientPhone;
    private String notes;

    public CompleteDeliveryDTO() {}

    public CompleteDeliveryDTO(String otp, String recipientName, String recipientPhone, String notes) {
        this.otp = otp;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.notes = notes;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
