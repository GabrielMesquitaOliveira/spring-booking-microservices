package com.bookingmicroservice.reservationservice.application.usecase;

import com.bookingmicroservice.reservationservice.application.dto.AvailableSlotResponse;
import com.bookingmicroservice.reservationservice.domain.entity.Reservation;
import com.bookingmicroservice.reservationservice.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAvailableSlotsUseCase {
    
    private final ReservationRepository reservationRepository;
    
    private static final int BUSINESS_HOURS_START = 8;
    private static final int BUSINESS_HOURS_END = 18;
    private static final int SLOT_DURATION_MINUTES = 30;
    
    public List<AvailableSlotResponse> execute(LocalDate date, String resourceName) {
        // Generate all possible time slots for the day
        List<AvailableSlotResponse> allSlots = generateAllSlots(date, resourceName);
        
        // Get existing reservations for the date
        LocalDateTime startOfDay = date.atTime(LocalTime.MIN);
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Reservation> existingReservations = reservationRepository
            .findByResourceNameAndDateBetween(resourceName, startOfDay, endOfDay);
        
        // Mark slots as unavailable if already booked
        return allSlots.stream()
            .map(slot -> {
                boolean isBooked = existingReservations.stream()
                    .anyMatch(r -> r.startDate().equals(slot.startTime()) && r.endDate().equals(slot.endTime()));
                
                return new AvailableSlotResponse(
                    slot.startTime(),
                    slot.endTime(),
                    slot.resourceName(),
                    !isBooked
                );
            })
            .toList();
    }
    
    private List<AvailableSlotResponse> generateAllSlots(LocalDate date, String resourceName) {
        List<AvailableSlotResponse> slots = new ArrayList<>();
        
        LocalDateTime currentSlot = date.atTime(BUSINESS_HOURS_START, 0);
        LocalDateTime endTime = date.atTime(BUSINESS_HOURS_END, 0);
        
        while (currentSlot.isBefore(endTime)) {
            LocalDateTime slotEnd = currentSlot.plusMinutes(SLOT_DURATION_MINUTES);
            
            slots.add(new AvailableSlotResponse(
                currentSlot,
                slotEnd,
                resourceName,
                true  // Initially all are available
            ));
            
            currentSlot = slotEnd;
        }
        
        return slots;
    }
}
