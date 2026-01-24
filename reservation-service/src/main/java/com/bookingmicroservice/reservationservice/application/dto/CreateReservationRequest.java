package com.bookingmicroservice.reservationservice.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateReservationRequest(
    @NotNull(message = "User ID is required")
    Long userId,
    
    @NotBlank(message = "Resource name is required")
    String resourceName,
    
    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    LocalDateTime startDate,
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    LocalDateTime endDate
) {}
