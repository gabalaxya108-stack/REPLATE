package com.replate.repository;

import com.replate.domain.entity.FoodDonation;
import com.replate.domain.enums.DonationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodDonationRepository extends JpaRepository<FoodDonation, Long>, JpaSpecificationExecutor<FoodDonation> {

    @Query("SELECT d FROM FoodDonation d JOIN FETCH d.donor dp JOIN FETCH dp.user u WHERE d.id = :id")
    Optional<FoodDonation> findByIdWithDonorDetails(@Param("id") Long id);

    @Query(value = "SELECT d FROM FoodDonation d WHERE d.donor.userId = :donorId ORDER BY d.createdAt DESC",
           countQuery = "SELECT COUNT(d) FROM FoodDonation d WHERE d.donor.userId = :donorId")
    Page<FoodDonation> findByDonorUserId(@Param("donorId") Long donorId, Pageable pageable);

    @Query("SELECT d FROM FoodDonation d WHERE d.id = :id AND d.donor.userId = :donorId")
    Optional<FoodDonation> findByIdAndDonorUserId(@Param("id") Long id, @Param("donorId") Long donorId);

    @Query("SELECT d FROM FoodDonation d WHERE d.status IN ('AVAILABLE', 'PARTIALLY_ALLOCATED') AND d.expiryTime <= :now")
    List<FoodDonation> findExpiredActiveDonations(@Param("now") Instant now);
}
