package com.bookingmicroservice.reservationservice.domain.entity;

import java.time.LocalDateTime;

public record Reservation(
    Long id,
    Long userId,
    String resourceName,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String status
) {}

