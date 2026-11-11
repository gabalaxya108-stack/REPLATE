package com.replate.repository;

import com.replate.domain.entity.VolunteerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, Long> {

    List<VolunteerProfile> findByVehicleType(String vehicleType);

    @Query("SELECT v FROM VolunteerProfile v JOIN FETCH v.user u WHERE u.status = 'ACTIVE' AND u.role = 'VOLUNTEER'")
    List<VolunteerProfile> findAllActive();

    @Query("SELECT v FROM VolunteerProfile v JOIN FETCH v.user u WHERE v.userId = :userId")
    Optional<VolunteerProfile> findByIdWithUser(@Param("userId") Long userId);
}
