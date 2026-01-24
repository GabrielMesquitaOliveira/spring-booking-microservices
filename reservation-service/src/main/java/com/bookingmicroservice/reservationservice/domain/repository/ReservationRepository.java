package com.bookingmicroservice.reservationservice.domain.repository;

import com.bookingmicroservice.reservationservice.domain.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findById(Long id);
    Reservation save(Reservation reservation);
    List<Reservation> findAll();
    List<Reservation> findByUserId(Long userId);
}
