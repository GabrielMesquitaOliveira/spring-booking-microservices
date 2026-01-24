package com.bookingmicroservice.userservice.domain.entity;

public record User(
    Long id,
    String username,
    String email,
    String fullName
) {}

