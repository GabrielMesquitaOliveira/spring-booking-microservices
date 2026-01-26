package com.bookingmicroservice.reservationservice.application.dto;

import java.time.LocalDateTime;

public record AvailableSlotResponse(
    LocalDateTime startTime,
    LocalDateTime endTime,
    String resourceName,
    boolean available
) {}
