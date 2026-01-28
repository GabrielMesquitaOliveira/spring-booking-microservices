package com.bookingmicroservice.reservationservice.controller;

import com.bookingmicroservice.reservationservice.TestReservationServiceApplication;
import com.bookingmicroservice.reservationservice.application.dto.CreateReservationRequest;
import com.bookingmicroservice.reservationservice.config.TestConfig;
import com.bookingmicroservice.reservationservice.infrastructure.client.UserServiceClient;
import com.bookingmicroservice.reservationservice.infrastructure.persistence.entity.ReservationJpaEntity;
import com.bookingmicroservice.reservationservice.infrastructure.persistence.repository.ReservationJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = {TestReservationServiceApplication.class},
        properties = {"spring.cloud.discovery.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import(TestConfig.class)
@Transactional
@DisplayName("ReservationController Integration Tests")
class ReservationControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reservationJpaRepository.deleteAll();
    }

    // ========== Create Reservation Tests ==========

    @Test
    @DisplayName("POST /api/reservations - Should create reservation with valid data and return 201")
    void shouldCreateReservationWithValidData() throws Exception {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 2, 15, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        CreateReservationRequest request = new CreateReservationRequest(
                1L, "Meeting Room A", start, end
        );

        // When & Then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.resourceName").value("Meeting Room A"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("POST /api/reservations - Should return 400 for invalid duration (not 30 min)")
    void shouldReturn400ForInvalidDuration() throws Exception {
        // Given - 60 minutes instead of 30
        LocalDateTime start = LocalDateTime.of(2026, 2, 15, 10, 0);
        LocalDateTime end = start.plusMinutes(60);
        CreateReservationRequest request = new CreateReservationRequest(
                1L, "Meeting Room A", start, end
        );

        // When & Then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Reservation Duration"))
                .andExpect(jsonPath("$.detail").value(containsString("must be exactly 30 minutes")));
    }

    @Test
    @DisplayName("POST /api/reservations - Should return 400 for invalid time slot (:15)")
    void shouldReturn400ForInvalidTimeSlot() throws Exception {
        // Given - starts at :15 instead of :00 or :30
        LocalDateTime start = LocalDateTime.of(2026, 2, 15, 10, 15);
        LocalDateTime end = start.plusMinutes(30);
        CreateReservationRequest request = new CreateReservationRequest(
                1L, "Meeting Room A", start, end
        );

        // When & Then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Time Slot"))
                .andExpect(jsonPath("$.detail").value(containsString("must start at :00 or :30")));
    }

    @Test
    @DisplayName("POST /api/reservations - Should return 400 for reservation outside business hours")
    void shouldReturn400ForOutsideBusinessHours() throws Exception {
        // Given - starts at 7:30 (before 8:00)
        LocalDateTime start = LocalDateTime.of(2026, 2, 15, 7, 30);
        LocalDateTime end = start.plusMinutes(30);
        CreateReservationRequest request = new CreateReservationRequest(
                1L, "Meeting Room A", start, end
        );

        // When & Then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Business Hours Violation"))
                .andExpect(jsonPath("$.detail").value(containsString("cannot start before 8:00")));
    }

    @Test
    @DisplayName("POST /api/reservations - Should return 409 for overlapping reservation")
    void shouldReturn409ForOverlappingReservation() throws Exception {
        // Given - existing reservation
        LocalDateTime start = LocalDateTime.of(2026, 2, 15, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        
        ReservationJpaEntity existing = new ReservationJpaEntity();
        existing.setUserId(2L);
        existing.setResourceName("Meeting Room A");
        existing.setStartDate(start);
        existing.setEndDate(end);
        existing.setStatus("CONFIRMED");
        reservationJpaRepository.save(existing);

        // When - try to create overlapping reservation
        CreateReservationRequest request = new CreateReservationRequest(
                1L, "Meeting Room A", start, end
        );

        // Then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Slot Already Booked"))
                .andExpect(jsonPath("$.detail").value(containsString("already booked")));
    }

    // ========== Get Reservation Tests ==========

    @Test
    @DisplayName("GET /api/reservations/{id} - Should return existing reservation")
    void shouldReturnExistingReservation() throws Exception {
        // Given
        LocalDateTime start = LocalDateTime.of(2026, 2, 15, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        
        ReservationJpaEntity saved = new ReservationJpaEntity();
        saved.setUserId(1L);
        saved.setResourceName("Meeting Room A");
        saved.setStartDate(start);
        saved.setEndDate(end);
        saved.setStatus("CONFIRMED");
        ReservationJpaEntity savedReservation = reservationJpaRepository.save(saved);

        // When & Then
        mockMvc.perform(get("/api/reservations/{id}", savedReservation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedReservation.getId()))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.resourceName").value("Meeting Room A"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /api/reservations - Should return list of all reservations")
    void shouldReturnAllReservations() throws Exception {
        // Given
        LocalDateTime start1 = LocalDateTime.of(2026, 2, 15, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2026, 2, 15, 11, 0);
        
        ReservationJpaEntity res1 = new ReservationJpaEntity();
        res1.setUserId(1L);
        res1.setResourceName("Room A");
        res1.setStartDate(start1);
        res1.setEndDate(start1.plusMinutes(30));
        res1.setStatus("CONFIRMED");
        
        ReservationJpaEntity res2 = new ReservationJpaEntity();
        res2.setUserId(1L);
        res2.setResourceName("Room B");
        res2.setStartDate(start2);
        res2.setEndDate(start2.plusMinutes(30));
        res2.setStatus("CONFIRMED");
        
        reservationJpaRepository.save(res1);
        reservationJpaRepository.save(res2);

        // When & Then
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].resourceName").value("Room A"))
                .andExpect(jsonPath("$[1].resourceName").value("Room B"));
    }

    @Test
    @DisplayName("GET /api/reservations/user/{userId} - Should return reservations for specific user")
    void shouldReturnReservationsByUserId() throws Exception {
        // Given
        LocalDateTime start1 = LocalDateTime.of(2026, 2, 15, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2026, 2, 15, 11, 0);
        
        ReservationJpaEntity user1Res = new ReservationJpaEntity();
        user1Res.setUserId(1L);
        user1Res.setResourceName("Room A");
        user1Res.setStartDate(start1);
        user1Res.setEndDate(start1.plusMinutes(30));
        user1Res.setStatus("CONFIRMED");
        
        ReservationJpaEntity user2Res = new ReservationJpaEntity();
        user2Res.setUserId(2L);
        user2Res.setResourceName("Room B");
        user2Res.setStartDate(start2);
        user2Res.setEndDate(start2.plusMinutes(30));
        user2Res.setStatus("CONFIRMED");
        
        reservationJpaRepository.save(user1Res);
        reservationJpaRepository.save(user2Res);

        // When & Then
        mockMvc.perform(get("/api/reservations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].resourceName").value("Room A"));
    }

    // ========== Available Slots Tests ==========

    @Test
    @DisplayName("GET /api/reservations/available-slots - Should return available time slots")
    void shouldReturnAvailableSlots() throws Exception {
        // Given - no existing reservations for this date/resource

        // When & Then
        mockMvc.perform(get("/api/reservations/available-slots")
                        .param("date", "2026-02-15")
                        .param("resourceName", "Meeting Room A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].startTime").exists())
                .andExpect(jsonPath("$[0].endTime").exists());
    }

    @Test
    @DisplayName("GET /api/reservations/available-slots - Should exclude booked slots")
    void shouldExcludeBookedSlots() throws Exception {
        // Given - book 10:00-10:30
        LocalDateTime start = LocalDateTime.of(2026, 2, 15, 10, 0);
        
        ReservationJpaEntity booked = new ReservationJpaEntity();
        booked.setUserId(1L);
        booked.setResourceName("Meeting Room A");
        booked.setStartDate(start);
        booked.setEndDate(start.plusMinutes(30));
        booked.setStatus("CONFIRMED");
        reservationJpaRepository.save(booked);

        // When & Then - available slots should not include 10:00-10:30
        mockMvc.perform(get("/api/reservations/available-slots")
                        .param("date", "2026-02-15")
                        .param("resourceName", "Meeting Room A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
