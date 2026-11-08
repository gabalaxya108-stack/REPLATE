package com.replate.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "ngo_profile")
public class NgoProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private BaseUser user;

    @NotBlank
    @Column(name = "ngo_name", nullable = false, length = 150)
    private String ngoName;

    @Column(name = "darpan_id", unique = true, length = 50)
    private String darpanId;

    @Column(name = "registration_80g", length = 50)
    private String registration80g;

    @Positive
    @Column(name = "daily_beneficiary_capacity", nullable = false)
    private Integer dailyBeneficiaryCapacity = 50;

    @Column(name = "verification_status", nullable = false, length = 30)
    private String verificationStatus = "PENDING";

    public NgoProfile() {}

    public NgoProfile(BaseUser user, String ngoName, String darpanId, Integer dailyCapacity) {
        this.user = user;
        this.userId = user.getId();
        this.ngoName = ngoName;
        this.darpanId = darpanId;
        this.dailyBeneficiaryCapacity = dailyCapacity;
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

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
