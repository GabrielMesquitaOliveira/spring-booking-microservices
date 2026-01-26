package com.bookingmicroservice.reservationservice.domain.exception;

public class InvalidTimeSlotException extends RuntimeException {
    public InvalidTimeSlotException(String message) {
        super(message);
    }
}
