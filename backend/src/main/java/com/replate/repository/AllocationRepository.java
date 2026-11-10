package com.replate.repository;

import com.replate.domain.entity.Allocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    @Query("SELECT a FROM Allocation a " +
           "JOIN FETCH a.donation d " +
           "JOIN FETCH d.donor dp " +
           "JOIN FETCH dp.user du " +
           "JOIN FETCH a.request r " +
           "JOIN FETCH r.ngo np " +
           "JOIN FETCH np.user nu " +
           "LEFT JOIN FETCH a.allocatedBy ab " +
           "WHERE a.id = :id")
    Optional<Allocation> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT a FROM Allocation a " +
           "JOIN FETCH a.donation d " +
           "JOIN FETCH a.request r " +
           "JOIN FETCH r.ngo np " +
           "WHERE a.donation.id = :donationId " +
           "ORDER BY a.createdAt DESC")
    List<Allocation> findByDonationId(@Param("donationId") Long donationId);

    @Query("SELECT a FROM Allocation a " +
           "JOIN FETCH a.donation d " +
           "JOIN FETCH a.request r " +
           "WHERE a.request.id = :requestId " +
           "ORDER BY a.createdAt DESC")
    List<Allocation> findByRequestId(@Param("requestId") Long requestId);

    @Query("SELECT COALESCE(SUM(a.allocatedQuantity), 0) FROM Allocation a " +
           "WHERE a.request.id = :requestId AND a.status != 'CANCELLED'")
    BigDecimal sumAllocatedQuantityByRequestId(@Param("requestId") Long requestId);

    @Query("SELECT COALESCE(SUM(a.allocatedQuantity), 0) FROM Allocation a " +
           "WHERE a.donation.id = :donationId AND a.status != 'CANCELLED'")
    BigDecimal sumAllocatedQuantityByDonationId(@Param("donationId") Long donationId);

    @Query(value = "SELECT a FROM Allocation a " +
                   "JOIN FETCH a.donation d " +
                   "JOIN FETCH a.request r " +
                   "JOIN FETCH r.ngo np " +
                   "WHERE d.donor.userId = :donorId " +
                   "ORDER BY a.createdAt DESC",
           countQuery = "SELECT COUNT(a) FROM Allocation a WHERE a.donation.donor.userId = :donorId")
    Page<Allocation> findByDonorUserId(@Param("donorId") Long donorId, Pageable pageable);

    @Query(value = "SELECT a FROM Allocation a " +
                   "JOIN FETCH a.donation d " +
                   "JOIN FETCH d.donor dp " +
                   "JOIN FETCH a.request r " +
                   "WHERE r.ngo.userId = :ngoId " +
                   "ORDER BY a.createdAt DESC",
           countQuery = "SELECT COUNT(a) FROM Allocation a WHERE a.request.ngo.userId = :ngoId")
    Page<Allocation> findByNgoUserId(@Param("ngoId") Long ngoId, Pageable pageable);
}
