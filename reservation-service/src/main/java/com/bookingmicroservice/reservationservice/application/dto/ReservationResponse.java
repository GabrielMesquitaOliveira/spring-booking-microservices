package com.bookingmicroservice.reservationservice.application.dto;

import java.time.LocalDateTime;

public record ReservationResponse(
    Long id,
    Long userId,
    String username,
    String resourceName,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String status
) {}
