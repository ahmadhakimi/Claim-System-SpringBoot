//ClaimRepository.java

package com.project.claim.system.repository;

import com.project.claim.system.entity.ClaimEntity;
import com.project.claim.system.enumeration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<ClaimEntity, UUID> {

    @Query("SELECT c FROM ClaimEntity c WHERE (:year is null or year(c.receiptDate) = :year) AND (:month is null or month(c.receiptDate) = :month) AND (:staffId is null or c.staff.id = :staffId) AND (:status is null or c.status = :status)")
    List<ClaimEntity> findAllByFilter(@Param("year") Integer year, @Param("month") Integer month, @Param("staffId") UUID staffId, @Param("status") Status status);


}
