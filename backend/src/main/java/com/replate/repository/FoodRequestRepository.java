package com.replate.repository;

import com.replate.domain.entity.FoodRequest;
import com.replate.domain.enums.RequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodRequestRepository extends JpaRepository<FoodRequest, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM FoodRequest r WHERE r.id = :id")
    Optional<FoodRequest> findByIdWithPessimisticLock(@Param("id") Long id);

    @Query(value = "SELECT r FROM FoodRequest r JOIN FETCH r.donation d JOIN FETCH d.donor dp JOIN FETCH dp.user du JOIN FETCH r.ngo np JOIN FETCH np.user nu WHERE r.id = :id")
    Optional<FoodRequest> findByIdWithDetails(@Param("id") Long id);

    @Query(value = "SELECT r FROM FoodRequest r JOIN FETCH r.donation d JOIN FETCH d.donor dp JOIN FETCH dp.user du WHERE r.ngo.userId = :ngoId ORDER BY r.createdAt DESC",
           countQuery = "SELECT COUNT(r) FROM FoodRequest r WHERE r.ngo.userId = :ngoId")
    Page<FoodRequest> findByNgoUserId(@Param("ngoId") Long ngoId, Pageable pageable);

    @Query(value = "SELECT r FROM FoodRequest r JOIN FETCH r.donation d JOIN FETCH r.ngo np JOIN FETCH np.user nu WHERE d.donor.userId = :donorId ORDER BY r.createdAt DESC",
           countQuery = "SELECT COUNT(r) FROM FoodRequest r WHERE r.donation.donor.userId = :donorId")
    Page<FoodRequest> findByDonorUserId(@Param("donorId") Long donorId, Pageable pageable);

    boolean existsByDonationIdAndNgoUserIdAndStatus(Long donationId, Long ngoUserId, RequestStatus status);
}

