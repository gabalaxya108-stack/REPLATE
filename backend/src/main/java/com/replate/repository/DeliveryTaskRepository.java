package com.replate.repository;

import com.replate.domain.entity.DeliveryTask;
import com.replate.domain.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, Long> {

    Optional<DeliveryTask> findByAllocationId(Long allocationId);

    boolean existsByAllocationId(Long allocationId);

    @Query("SELECT COUNT(d) > 0 FROM DeliveryTask d WHERE d.allocation.id = :allocationId AND d.status NOT IN :terminalStatuses")
    boolean existsActiveByAllocationId(@Param("allocationId") Long allocationId,
                                       @Param("terminalStatuses") Collection<DeliveryStatus> terminalStatuses);

    @Query("SELECT d FROM DeliveryTask d " +
            "JOIN FETCH d.allocation a " +
            "JOIN FETCH a.donation f " +
            "JOIN FETCH f.donor dp " +
            "JOIN FETCH dp.user du " +
            "JOIN FETCH a.request r " +
            "JOIN FETCH r.ngo np " +
            "JOIN FETCH np.user nu " +
            "LEFT JOIN FETCH d.volunteer v " +
            "LEFT JOIN FETCH v.user vu " +
            "WHERE d.id = :id")
    Optional<DeliveryTask> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT d FROM DeliveryTask d " +
            "JOIN FETCH d.allocation a " +
            "JOIN FETCH a.donation f " +
            "JOIN FETCH f.donor dp " +
            "JOIN FETCH dp.user du " +
            "JOIN FETCH a.request r " +
            "JOIN FETCH r.ngo np " +
            "JOIN FETCH np.user nu " +
            "WHERE d.volunteer.userId = :volunteerUserId " +
            "ORDER BY d.createdAt DESC")
    List<DeliveryTask> findByVolunteerUserId(@Param("volunteerUserId") Long volunteerUserId);

    @Query("SELECT d FROM DeliveryTask d " +
            "JOIN FETCH d.allocation a " +
            "JOIN FETCH a.donation f " +
            "JOIN FETCH f.donor dp " +
            "JOIN FETCH dp.user du " +
            "JOIN FETCH a.request r " +
            "JOIN FETCH r.ngo np " +
            "JOIN FETCH np.user nu " +
            "LEFT JOIN FETCH d.volunteer v " +
            "LEFT JOIN FETCH v.user vu " +
            "WHERE dp.userId = :donorUserId " +
            "ORDER BY d.createdAt DESC")
    List<DeliveryTask> findByDonorUserId(@Param("donorUserId") Long donorUserId);

    @Query("SELECT d FROM DeliveryTask d " +
            "JOIN FETCH d.allocation a " +
            "JOIN FETCH a.donation f " +
            "JOIN FETCH f.donor dp " +
            "JOIN FETCH dp.user du " +
            "JOIN FETCH a.request r " +
            "JOIN FETCH r.ngo np " +
            "JOIN FETCH np.user nu " +
            "LEFT JOIN FETCH d.volunteer v " +
            "LEFT JOIN FETCH v.user vu " +
            "WHERE np.userId = :ngoUserId " +
            "ORDER BY d.createdAt DESC")
    List<DeliveryTask> findByNgoUserId(@Param("ngoUserId") Long ngoUserId);

    @Query("SELECT d FROM DeliveryTask d " +
            "JOIN FETCH d.allocation a " +
            "JOIN FETCH a.donation f " +
            "JOIN FETCH f.donor dp " +
            "JOIN FETCH dp.user du " +
            "JOIN FETCH a.request r " +
            "JOIN FETCH r.ngo np " +
            "JOIN FETCH np.user nu " +
            "LEFT JOIN FETCH d.volunteer v " +
            "LEFT JOIN FETCH v.user vu " +
            "WHERE d.status IN :statuses " +
            "ORDER BY d.createdAt DESC")
    List<DeliveryTask> findByStatusIn(@Param("statuses") Collection<DeliveryStatus> statuses);

    @Query("SELECT d FROM DeliveryTask d " +
            "JOIN FETCH d.allocation a " +
            "JOIN FETCH a.donation f " +
            "JOIN FETCH f.donor dp " +
            "JOIN FETCH dp.user du " +
            "JOIN FETCH a.request r " +
            "JOIN FETCH r.ngo np " +
            "JOIN FETCH np.user nu " +
            "WHERE d.status IN ('UNASSIGNED', 'PENDING_ASSIGNMENT', 'REJECTED') " +
            "ORDER BY d.createdAt DESC")
    List<DeliveryTask> findAvailableForAssignment();
}

