package com.bookingmicroservice.reservationservice.presentation.controller;

import com.bookingmicroservice.reservationservice.application.dto.AvailableSlotResponse;
import com.bookingmicroservice.reservationservice.application.dto.CreateReservationRequest;
import com.bookingmicroservice.reservationservice.application.dto.ReservationResponse;
import com.bookingmicroservice.reservationservice.application.usecase.CreateReservationUseCase;
import com.bookingmicroservice.reservationservice.application.usecase.GetAvailableSlotsUseCase;
import com.bookingmicroservice.reservationservice.application.usecase.GetReservationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    private final CreateReservationUseCase createReservationUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final GetAvailableSlotsUseCase getAvailableSlotsUseCase;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@Valid @RequestBody CreateReservationRequest request) {
        return createReservationUseCase.execute(request);
    }
    
    @GetMapping("/{id}")
    public ReservationResponse getReservation(@PathVariable Long id) {
        return getReservationUseCase.execute(id);
    }
    
    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return getReservationUseCase.executeAll();
    }
    
    @GetMapping("/user/{userId}")
    public List<ReservationResponse> getReservationsByUserId(@PathVariable Long userId) {
        return getReservationUseCase.executeByUserId(userId);
    }
    
    @GetMapping("/available-slots")
    public List<AvailableSlotResponse> getAvailableSlots(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(defaultValue = "Sala A") String resourceName
    ) {
        return getAvailableSlotsUseCase.execute(date, resourceName);
    }
}
