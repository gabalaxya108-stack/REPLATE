package com.replate.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Demonstrates Java OOP Composition.
 * Value object embedded in entities requiring geographic location.
 */
@Embeddable
public class LocationCoordinate implements Serializable {

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "city", length = 80)
    private String city;

    @Column(name = "state", length = 80)
    private String state;

    @Column(name = "pincode", length = 10)
    private String pincode;

    // Constructors demonstrating Constructor Overloading
    public LocationCoordinate() {}

    public LocationCoordinate(String addressLine1, String city, String state, String pincode, BigDecimal latitude, BigDecimal longitude) {
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getFormattedAddress() {
        return String.format("%s, %s, %s - %s", addressLine1, city, state, pincode);
    }
}
