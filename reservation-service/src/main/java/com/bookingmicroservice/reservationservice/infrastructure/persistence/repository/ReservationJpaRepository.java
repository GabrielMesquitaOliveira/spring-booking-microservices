package com.bookingmicroservice.reservationservice.infrastructure.persistence.repository;

import com.bookingmicroservice.reservationservice.infrastructure.persistence.entity.ReservationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {
    List<ReservationJpaEntity> findByUserId(Long userId);
    
    @Query("SELECT r FROM ReservationJpaEntity r WHERE r.resourceName = :resourceName " +
           "AND r.startDate >= :startOfDay AND r.endDate <= :endOfDay")
    List<ReservationJpaEntity> findByResourceNameAndDateBetween(
        @Param("resourceName") String resourceName,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );
}
