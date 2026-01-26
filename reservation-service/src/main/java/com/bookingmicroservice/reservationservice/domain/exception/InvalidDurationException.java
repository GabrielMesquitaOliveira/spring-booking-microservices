package com.bookingmicroservice.reservationservice.domain.exception;

public class InvalidDurationException extends RuntimeException {
    public InvalidDurationException(String message) {
        super(message);
    }
}
