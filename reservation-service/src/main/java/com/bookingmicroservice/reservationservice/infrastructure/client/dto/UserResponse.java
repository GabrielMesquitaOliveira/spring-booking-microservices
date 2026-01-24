package com.bookingmicroservice.reservationservice.infrastructure.client.dto;

public record UserResponse(
    Long id,
    String username,
    String email,
    String fullName
) {}
