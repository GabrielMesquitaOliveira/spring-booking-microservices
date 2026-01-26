package com.bookingmicroservice.reservationservice.presentation.exception;

import com.bookingmicroservice.reservationservice.domain.exception.BusinessHoursViolationException;
import com.bookingmicroservice.reservationservice.domain.exception.InvalidDurationException;
import com.bookingmicroservice.reservationservice.domain.exception.InvalidTimeSlotException;
import com.bookingmicroservice.reservationservice.domain.exception.SlotAlreadyBookedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(InvalidDurationException.class)
    public ProblemDetail handleInvalidDuration(InvalidDurationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, 
            ex.getMessage()
        );
        problemDetail.setTitle("Invalid Reservation Duration");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
    
    @ExceptionHandler(InvalidTimeSlotException.class)
    public ProblemDetail handleInvalidTimeSlot(InvalidTimeSlotException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, 
            ex.getMessage()
        );
        problemDetail.setTitle("Invalid Time Slot");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
    
    @ExceptionHandler(BusinessHoursViolationException.class)
    public ProblemDetail handleBusinessHoursViolation(BusinessHoursViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, 
            ex.getMessage()
        );
        problemDetail.setTitle("Business Hours Violation");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
    
    @ExceptionHandler(SlotAlreadyBookedException.class)
    public ProblemDetail handleSlotAlreadyBooked(SlotAlreadyBookedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, 
            ex.getMessage()
        );
        problemDetail.setTitle("Slot Already Booked");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
}
