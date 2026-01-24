package com.bookingmicroservice.reservationservice.infrastructure.persistence.repository;

import com.bookingmicroservice.reservationservice.infrastructure.persistence.entity.ReservationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {
    List<ReservationJpaEntity> findByUserId(Long userId);
}
