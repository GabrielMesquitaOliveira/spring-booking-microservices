package com.bookingmicroservice.reservationservice.service;

import com.bookingmicroservice.reservationservice.application.dto.CreateReservationRequest;
import com.bookingmicroservice.reservationservice.application.dto.ReservationResponse;
import com.bookingmicroservice.reservationservice.application.mapper.ReservationMapper;
import com.bookingmicroservice.reservationservice.application.usecase.CreateReservationUseCase;
import com.bookingmicroservice.reservationservice.domain.entity.Reservation;
import com.bookingmicroservice.reservationservice.domain.exception.SlotAlreadyBookedException;
import com.bookingmicroservice.reservationservice.domain.repository.ReservationRepository;
import com.bookingmicroservice.reservationservice.domain.service.ReservationValidationService;
import com.bookingmicroservice.reservationservice.infrastructure.client.UserServiceClient;
import com.bookingmicroservice.reservationservice.infrastructure.client.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateReservationUseCase Unit Tests")
class CreateReservationUseCaseTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ReservationValidationService validationService;

    @InjectMocks
    private CreateReservationUseCase createReservationUseCase;

    private CreateReservationRequest validRequest;
    private UserResponse userResponse;
    private Reservation reservation;
    private Reservation savedReservation;
    private ReservationResponse reservationResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.of(2026, 2, 15, 10, 0);
        LocalDateTime end = start.plusMinutes(30);

        validRequest = new CreateReservationRequest(
                1L,
                "Meeting Room A",
                start,
                end
        );

        userResponse = new UserResponse(1L, "johndoe", "john@example.com", "John Doe");

        reservation = new Reservation(null, 1L, "Meeting Room A", start, end, "CONFIRMED");
        savedReservation = new Reservation(1L, 1L, "Meeting Room A", start, end, "CONFIRMED");

        reservationResponse = new ReservationResponse(
                1L, 1L, "johndoe", "Meeting Room A", start, end, "CONFIRMED"
        );
    }

    @Test
    @DisplayName("Should successfully create reservation with valid data")
    void shouldCreateReservationWithValidData() {
        // Given
        when(userServiceClient.getUserById(1L)).thenReturn(userResponse);
        when(reservationRepository.findByResourceNameAndDateBetween(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationMapper.toDomain(validRequest)).thenReturn(reservation);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(reservationMapper.toResponseWithUsername(savedReservation, "johndoe"))
                .thenReturn(reservationResponse);

        // When
        ReservationResponse result = createReservationUseCase.execute(validRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("johndoe");
        assertThat(result.resourceName()).isEqualTo("Meeting Room A");
        assertThat(result.status()).isEqualTo("CONFIRMED");

        verify(userServiceClient).getUserById(1L);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should validate user exists before creating reservation")
    void shouldValidateUserExists() {
        // Given
        when(userServiceClient.getUserById(1L)).thenReturn(userResponse);
        when(reservationRepository.findByResourceNameAndDateBetween(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationMapper.toDomain(validRequest)).thenReturn(reservation);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(reservationMapper.toResponseWithUsername(any(), any())).thenReturn(reservationResponse);

        // When
        createReservationUseCase.execute(validRequest);

        // Then
        verify(userServiceClient, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("Should call all validation methods")
    void shouldCallAllValidationMethods() {
        // Given
        when(userServiceClient.getUserById(1L)).thenReturn(userResponse);
        when(reservationRepository.findByResourceNameAndDateBetween(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationMapper.toDomain(validRequest)).thenReturn(reservation);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(reservationMapper.toResponseWithUsername(any(), any())).thenReturn(reservationResponse);

        // When
        createReservationUseCase.execute(validRequest);

        // Then
        verify(validationService).validateDuration(validRequest.startDate(), validRequest.endDate());
        verify(validationService).validateTimeSlot(validRequest.startDate());
        verify(validationService).validateBusinessHours(validRequest.startDate(), validRequest.endDate());
        verify(validationService).validateNoOverlap(
                eq(validRequest.resourceName()),
                eq(validRequest.startDate()),
                eq(validRequest.endDate()),
                any(List.class)
        );
    }

    @Test
    @DisplayName("Should check for overlapping reservations")
    void shouldCheckForOverlappingReservations() {
        // Given
        when(userServiceClient.getUserById(1L)).thenReturn(userResponse);
        when(reservationRepository.findByResourceNameAndDateBetween(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationMapper.toDomain(validRequest)).thenReturn(reservation);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(reservationMapper.toResponseWithUsername(any(), any())).thenReturn(reservationResponse);

        // When
        createReservationUseCase.execute(validRequest);

        // Then
        verify(reservationRepository).findByResourceNameAndDateBetween(
                eq("Meeting Room A"),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("Should throw exception when slot is already booked")
    void shouldThrowExceptionWhenSlotAlreadyBooked() {
        // Given
        LocalDateTime start = validRequest.startDate();
        LocalDateTime end = validRequest.endDate();
        List<Reservation> existingReservations = List.of(
                new Reservation(2L, 2L, "Meeting Room A", start, end, "CONFIRMED")
        );

        when(userServiceClient.getUserById(1L)).thenReturn(userResponse);
        when(reservationRepository.findByResourceNameAndDateBetween(any(), any(), any()))
                .thenReturn(existingReservations);

        doThrow(new SlotAlreadyBookedException("Slot already booked"))
                .when(validationService).validateNoOverlap(
                        eq("Meeting Room A"),
                        eq(start),
                        eq(end),
                        eq(existingReservations)
                );

        // When & Then
        assertThatThrownBy(() -> createReservationUseCase.execute(validRequest))
                .isInstanceOf(SlotAlreadyBookedException.class)
                .hasMessageContaining("Slot already booked");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save reservation with CONFIRMED status")
    void shouldSaveReservationWithConfirmedStatus() {
        // Given
        when(userServiceClient.getUserById(1L)).thenReturn(userResponse);
        when(reservationRepository.findByResourceNameAndDateBetween(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationMapper.toDomain(validRequest)).thenReturn(reservation);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(reservationMapper.toResponseWithUsername(any(), any())).thenReturn(reservationResponse);

        // When
        createReservationUseCase.execute(validRequest);

        // Then
        verify(reservationRepository).save(argThat(res ->
                res.status().equals("CONFIRMED")
        ));
    }

    @Test
    @DisplayName("Should return response with username from user service")
    void shouldReturnResponseWithUsername() {
        // Given
        when(userServiceClient.getUserById(1L)).thenReturn(userResponse);
        when(reservationRepository.findByResourceNameAndDateBetween(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(reservationMapper.toDomain(validRequest)).thenReturn(reservation);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(reservationMapper.toResponseWithUsername(savedReservation, "johndoe"))
                .thenReturn(reservationResponse);

        // When
        ReservationResponse result = createReservationUseCase.execute(validRequest);

        // Then
        assertThat(result.username()).isEqualTo("johndoe");
        verify(reservationMapper).toResponseWithUsername(savedReservation, "johndoe");
    }
}
