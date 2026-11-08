package com.replate.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "volunteer_profile")
public class VolunteerProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private BaseUser user;

    @NotBlank
    @Column(name = "vehicle_type", nullable = false, length = 40)
    private String vehicleType;

    @NotBlank
    @Column(name = "emergency_contact", nullable = false, length = 20)
    private String emergencyContact;

    @PositiveOrZero
    @Column(name = "total_deliveries", nullable = false)
    private Integer totalDeliveries = 0;

    public VolunteerProfile() {}

    public VolunteerProfile(BaseUser user, String vehicleType, String emergencyContact) {
        this.user = user;
        this.userId = user.getId();
        this.vehicleType = vehicleType;
        this.emergencyContact = emergencyContact;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BaseUser getUser() {
        return user;
    }

    public void setUser(BaseUser user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
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

    public void incrementDeliveries() {
        this.totalDeliveries++;
    }
}
