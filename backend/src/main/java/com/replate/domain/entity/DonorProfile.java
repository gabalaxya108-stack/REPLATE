package com.replate.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Persistable;

/**
 * Demonstrates Java OOP One-to-One Association and Encapsulation.
 */
@Entity
@Table(name = "donor_profile")
public class DonorProfile implements Persistable<Long> {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private BaseUser user;

    @Transient
    private boolean isNew = true;

    @NotBlank
    @Column(name = "organization_name", nullable = false, length = 150)
    private String organizationName;

    @NotBlank
    @Column(name = "organization_type", nullable = false, length = 50)
    private String organizationType;

    @NotBlank
    @Column(name = "fssai_license_no", nullable = false, unique = true, length = 30)
    private String fssaiLicenseNo;

    @Column(name = "operating_hours", length = 100)
    private String operatingHours;

    public DonorProfile() {}

    public DonorProfile(BaseUser user, String organizationName, String organizationType, String fssaiLicenseNo) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
        this.organizationName = organizationName;
        this.organizationType = organizationType;
        this.fssaiLicenseNo = fssaiLicenseNo;
    }

    @Override
    public Long getId() {
        return this.userId;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
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
}

