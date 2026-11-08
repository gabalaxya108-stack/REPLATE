package com.replate.repository;

import com.replate.domain.entity.VolunteerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, Long> {

    List<VolunteerProfile> findByVehicleType(String vehicleType);
}

