package com.replate.dto.request;

import com.replate.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class RegisterRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Name must not exceed 120 characters")
    private String fullName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
    private String phoneNumber;

    @NotNull(message = "Role is required (DONOR, NGO, or VOLUNTEER)")
    private UserRole role;

    @NotBlank(message = "Address is required")
    private String addressLine1;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Size(min = 6, max = 10, message = "Pincode must be between 6 and 10 characters")
    private String pincode;

    private BigDecimal latitude;
    private BigDecimal longitude;

    // Donor Specific Fields
    private String organizationName;
    private String organizationType;
    private String fssaiLicenseNo;
    private String operatingHours;

    // NGO Specific Fields
    private String ngoName;
    private String darpanId;
    private String registration80g;
    private Integer dailyBeneficiaryCapacity;

    // Volunteer Specific Fields
    private String vehicleType;
    private String emergencyContact;

    public RegisterRequestDTO() {}

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getFssaiLicenseNo() {
        return fssaiLicenseNo;
    }

    public void setFssaiLicenseNo(String fssaiLicenseNo) {
        this.fssaiLicenseNo = fssaiLicenseNo;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }

    public String getDarpanId() {
        return darpanId;
    }

    public void setDarpanId(String darpanId) {
        this.darpanId = darpanId;
    }

    public String getRegistration80g() {
        return registration80g;
    }

    public void setRegistration80g(String registration80g) {
        this.registration80g = registration80g;
    }

    public Integer getDailyBeneficiaryCapacity() {
        return dailyBeneficiaryCapacity;
    }

    public void setDailyBeneficiaryCapacity(Integer dailyBeneficiaryCapacity) {
        this.dailyBeneficiaryCapacity = dailyBeneficiaryCapacity;
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
}
