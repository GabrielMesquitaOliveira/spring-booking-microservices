package com.bookingmicroservice.reservationservice.domain.service;

import com.bookingmicroservice.reservationservice.domain.entity.Reservation;
import com.bookingmicroservice.reservationservice.domain.exception.BusinessHoursViolationException;
import com.bookingmicroservice.reservationservice.domain.exception.InvalidDurationException;
import com.bookingmicroservice.reservationservice.domain.exception.InvalidTimeSlotException;
import com.bookingmicroservice.reservationservice.domain.exception.SlotAlreadyBookedException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationValidationService {
    
    private static final int SLOT_DURATION_MINUTES = 30;
    private static final int BUSINESS_HOURS_START = 8;
    private static final int BUSINESS_HOURS_END = 18;
    
    public void validateDuration(LocalDateTime startDate, LocalDateTime endDate) {
        Duration duration = Duration.between(startDate, endDate);
        long minutes = duration.toMinutes();
        
        if (minutes != SLOT_DURATION_MINUTES) {
            throw new InvalidDurationException(
                String.format("Reservation duration must be exactly %d minutes. Got: %d minutes", 
                    SLOT_DURATION_MINUTES, minutes)
            );
        }
    }
    
    public void validateTimeSlot(LocalDateTime startDate) {
        int minute = startDate.getMinute();
        
        if (minute != 0 && minute != 30) {
            throw new InvalidTimeSlotException(
                String.format("Reservations must start at :00 or :30. Got: %02d minutes", minute)
            );
        }
    }
    
    public void validateBusinessHours(LocalDateTime startDate, LocalDateTime endDate) {
        int startHour = startDate.getHour();
        int endHour = endDate.getHour();
        int endMinute = endDate.getMinute();
        
        if (startHour < BUSINESS_HOURS_START) {
            throw new BusinessHoursViolationException(
                String.format("Reservations cannot start before %d:00. Start time: %s", 
                    BUSINESS_HOURS_START, startDate)
            );
        }
        
        if (endHour > BUSINESS_HOURS_END || (endHour == BUSINESS_HOURS_END && endMinute > 0)) {
            throw new BusinessHoursViolationException(
                String.format("Reservations cannot end after %d:00. End time: %s", 
                    BUSINESS_HOURS_END, endDate)
            );
        }
    }
    
    public void validateNoOverlap(String resourceName, LocalDateTime startDate, LocalDateTime endDate, 
                                   List<Reservation> existingReservations) {
        boolean hasOverlap = existingReservations.stream()
            .filter(r -> r.resourceName().equals(resourceName))
            .anyMatch(r -> isOverlapping(startDate, endDate, r.startDate(), r.endDate()));
        
        if (hasOverlap) {
            throw new SlotAlreadyBookedException(
                String.format("Time slot %s to %s is already booked for resource '%s'", 
                    startDate, endDate, resourceName)
            );
        }
    }
    
    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, 
                                  LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
