package com.replate.repository;

import com.replate.domain.entity.DonorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonorProfileRepository extends JpaRepository<DonorProfile, Long> {

    Optional<DonorProfile> findByFssaiLicenseNo(String fssaiLicenseNo);

    boolean existsByFssaiLicenseNo(String fssaiLicenseNo);
}

