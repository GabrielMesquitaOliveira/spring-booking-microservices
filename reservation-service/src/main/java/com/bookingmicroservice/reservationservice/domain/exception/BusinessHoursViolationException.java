package com.bookingmicroservice.reservationservice.domain.exception;

public class BusinessHoursViolationException extends RuntimeException {
    public BusinessHoursViolationException(String message) {
        super(message);
    }
}
