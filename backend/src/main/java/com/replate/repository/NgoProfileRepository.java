package com.replate.repository;

import com.replate.domain.entity.NgoProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NgoProfileRepository extends JpaRepository<NgoProfile, Long> {

    Optional<NgoProfile> findByDarpanId(String darpanId);

    boolean existsByDarpanId(String darpanId);

    List<NgoProfile> findByVerificationStatus(String verificationStatus);
}
