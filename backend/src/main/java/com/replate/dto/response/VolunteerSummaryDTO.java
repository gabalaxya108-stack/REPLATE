package com.replate.dto.response;

import com.replate.domain.entity.VolunteerProfile;

public class VolunteerSummaryDTO {

    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String vehicleType;
    private String emergencyContact;
    private Integer totalDeliveries;
    private String city;
    private String state;
    private String status;

    public VolunteerSummaryDTO() {}

    public static VolunteerSummaryDTO fromEntity(VolunteerProfile profile) {
        if (profile == null) return null;
        VolunteerSummaryDTO dto = new VolunteerSummaryDTO();
        dto.setUserId(profile.getUserId());
        dto.setVehicleType(profile.getVehicleType());
        dto.setEmergencyContact(profile.getEmergencyContact());
        dto.setTotalDeliveries(profile.getTotalDeliveries() != null ? profile.getTotalDeliveries() : 0);
        if (profile.getUser() != null) {
            dto.setFullName(profile.getUser().getFullName());
            dto.setEmail(profile.getUser().getEmail());
            dto.setPhoneNumber(profile.getUser().getPhoneNumber());
            if (profile.getUser().getLocation() != null) {
                dto.setCity(profile.getUser().getLocation().getCity());
                dto.setState(profile.getUser().getLocation().getState());
            }
            dto.setStatus(profile.getUser().getStatus() != null ? profile.getUser().getStatus() : "ACTIVE");
        }
        return dto;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public Integer getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(Integer totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
