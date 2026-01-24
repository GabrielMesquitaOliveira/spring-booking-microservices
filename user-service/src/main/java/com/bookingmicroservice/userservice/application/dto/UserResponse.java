package com.bookingmicroservice.userservice.application.dto;

public record UserResponse(
    Long id,
    String username,
    String email,
    String fullName
) {}
