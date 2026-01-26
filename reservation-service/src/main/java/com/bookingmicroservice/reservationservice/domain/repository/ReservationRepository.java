package com.bookingmicroservice.reservationservice.domain.repository;

import com.bookingmicroservice.reservationservice.domain.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findById(Long id);
    Reservation save(Reservation reservation);
    List<Reservation> findAll();
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByResourceNameAndDateBetween(String resourceName, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
