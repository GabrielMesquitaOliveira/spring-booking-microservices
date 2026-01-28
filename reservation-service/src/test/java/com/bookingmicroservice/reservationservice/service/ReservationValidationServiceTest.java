package com.bookingmicroservice.reservationservice.service;

import com.bookingmicroservice.reservationservice.domain.entity.Reservation;
import com.bookingmicroservice.reservationservice.domain.exception.BusinessHoursViolationException;
import com.bookingmicroservice.reservationservice.domain.exception.InvalidDurationException;
import com.bookingmicroservice.reservationservice.domain.exception.InvalidTimeSlotException;
import com.bookingmicroservice.reservationservice.domain.exception.SlotAlreadyBookedException;
import com.bookingmicroservice.reservationservice.domain.service.ReservationValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("ReservationValidationService Unit Tests")
class ReservationValidationServiceTest {

    private ReservationValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ReservationValidationService();
    }

    // ========== Duration Validation Tests ==========

    @Test
    @DisplayName("Should accept exactly 30 minutes duration")
    void shouldAcceptExactly30MinutesDuration() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 0);
        LocalDateTime end = start.plusMinutes(30);

        // When & Then
        assertDoesNotThrow(() -> validationService.validateDuration(start, end));
    }

    @Test
    @DisplayName("Should reject duration less than 30 minutes")
    void shouldRejectDurationLessThan30Minutes() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 0);
        LocalDateTime end = start.plusMinutes(15);

        // When & Then
        assertThatThrownBy(() -> validationService.validateDuration(start, end))
                .isInstanceOf(InvalidDurationException.class)
                .hasMessageContaining("must be exactly 30 minutes")
                .hasMessageContaining("Got: 15 minutes");
    }

    @Test
    @DisplayName("Should reject duration more than 30 minutes")
    void shouldRejectDurationMoreThan30Minutes() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 0);
        LocalDateTime end = start.plusMinutes(60);

        // When & Then
        assertThatThrownBy(() -> validationService.validateDuration(start, end))
                .isInstanceOf(InvalidDurationException.class)
                .hasMessageContaining("must be exactly 30 minutes")
                .hasMessageContaining("Got: 60 minutes");
    }

    // ========== Time Slot Validation Tests ==========

    @Test
    @DisplayName("Should accept time slot starting at :00")
    void shouldAcceptTimeSlotStartingAtZero() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 0);

        // When & Then
        assertDoesNotThrow(() -> validationService.validateTimeSlot(start));
    }

    @Test
    @DisplayName("Should accept time slot starting at :30")
    void shouldAcceptTimeSlotStartingAt30() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 30);

        // When & Then
        assertDoesNotThrow(() -> validationService.validateTimeSlot(start));
    }

    @Test
    @DisplayName("Should reject time slot starting at :15")
    void shouldRejectTimeSlotStartingAt15() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 15);

        // When & Then
        assertThatThrownBy(() -> validationService.validateTimeSlot(start))
                .isInstanceOf(InvalidTimeSlotException.class)
                .hasMessageContaining("must start at :00 or :30")
                .hasMessageContaining("Got: 15 minutes");
    }

    @Test
    @DisplayName("Should reject time slot starting at :45")
    void shouldRejectTimeSlotStartingAt45() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 45);

        // When & Then
        assertThatThrownBy(() -> validationService.validateTimeSlot(start))
                .isInstanceOf(InvalidTimeSlotException.class)
                .hasMessageContaining("must start at :00 or :30")
                .hasMessageContaining("Got: 45 minutes");
    }

    // ========== Business Hours Validation Tests ==========

    @Test
    @DisplayName("Should accept reservation at business hours start (8:00)")
    void shouldAcceptReservationAtBusinessHoursStart() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 8, 0);
        LocalDateTime end = start.plusMinutes(30);

        // When & Then
        assertDoesNotThrow(() -> validationService.validateBusinessHours(start, end));
    }

    @Test
    @DisplayName("Should accept reservation ending at business hours end (18:00)")
    void shouldAcceptReservationEndingAtBusinessHoursEnd() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 17, 30);
        LocalDateTime end = LocalDateTime.of(2026, 1, 28, 18, 0);

        // When & Then
        assertDoesNotThrow(() -> validationService.validateBusinessHours(start, end));
    }

    @Test
    @DisplayName("Should reject reservation starting before business hours")
    void shouldRejectReservationStartingBeforeBusinessHours() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 7, 30);
        LocalDateTime end = start.plusMinutes(30);

        // When & Then
        assertThatThrownBy(() -> validationService.validateBusinessHours(start, end))
                .isInstanceOf(BusinessHoursViolationException.class)
                .hasMessageContaining("cannot start before 8:00");
    }

    @Test
    @DisplayName("Should reject reservation ending after business hours")
    void shouldRejectReservationEndingAfterBusinessHours() {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 18, 0);
        LocalDateTime end = start.plusMinutes(30);

        // When & Then
        assertThatThrownBy(() -> validationService.validateBusinessHours(start, end))
                .isInstanceOf(BusinessHoursViolationException.class)
                .hasMessageContaining("cannot end after 18:00");
    }

    // ========== Overlap Validation Tests ==========

    @Test
    @DisplayName("Should accept reservation when no existing reservations")
    void shouldAcceptReservationWhenNoExistingReservations() {
        // Given
        String resource = "Meeting Room A";
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        List<Reservation> existingReservations = new ArrayList<>();

        // When & Then
        assertDoesNotThrow(() -> 
            validationService.validateNoOverlap(resource, start, end, existingReservations)
        );
    }

    @Test
    @DisplayName("Should accept reservation when no overlap with existing reservations")
    void shouldAcceptReservationWhenNoOverlap() {
        // Given
        String resource = "Meeting Room A";
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        
        List<Reservation> existingReservations = List.of(
            new Reservation(1L, 1L, resource, 
                LocalDateTime.of(2026, 1, 28, 9, 0),
                LocalDateTime.of(2026, 1, 28, 9, 30),
                "CONFIRMED"),
            new Reservation(2L, 2L, resource,
                LocalDateTime.of(2026, 1, 28, 11, 0),
                LocalDateTime.of(2026, 1, 28, 11, 30),
                "CONFIRMED")
        );

        // When & Then
        assertDoesNotThrow(() -> 
            validationService.validateNoOverlap(resource, start, end, existingReservations)
        );
    }

    @Test
    @DisplayName("Should accept reservation for different resource at same time")
    void shouldAcceptReservationForDifferentResource() {
        // Given
        String resource = "Meeting Room B";
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        
        List<Reservation> existingReservations = List.of(
            new Reservation(1L, 1L, "Meeting Room A", start, end, "CONFIRMED")
        );

        // When & Then
        assertDoesNotThrow(() -> 
            validationService.validateNoOverlap(resource, start, end, existingReservations)
        );
    }

    @Test
    @DisplayName("Should reject reservation with exact overlap")
    void shouldRejectReservationWithExactOverlap() {
        // Given
        String resource = "Meeting Room A";
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        
        List<Reservation> existingReservations = List.of(
            new Reservation(1L, 1L, resource, start, end, "CONFIRMED")
        );

        // When & Then
        assertThatThrownBy(() -> 
            validationService.validateNoOverlap(resource, start, end, existingReservations)
        )
                .isInstanceOf(SlotAlreadyBookedException.class)
                .hasMessageContaining("already booked")
                .hasMessageContaining(resource);
    }

    @Test
    @DisplayName("Should reject reservation with partial overlap")
    void shouldRejectReservationWithPartialOverlap() {
        // Given
        String resource = "Meeting Room A";
        LocalDateTime start = LocalDateTime.of(2026, 1, 28, 10, 15);
        LocalDateTime end = start.plusMinutes(30);
        
        List<Reservation> existingReservations = List.of(
            new Reservation(1L, 1L, resource,
                LocalDateTime.of(2026, 1, 28, 10, 0),
                LocalDateTime.of(2026, 1, 28, 10, 30),
                "CONFIRMED")
        );

        // When & Then
        assertThatThrownBy(() -> 
            validationService.validateNoOverlap(resource, start, end, existingReservations)
        )
                .isInstanceOf(SlotAlreadyBookedException.class)
                .hasMessageContaining("already booked");
    }
}
