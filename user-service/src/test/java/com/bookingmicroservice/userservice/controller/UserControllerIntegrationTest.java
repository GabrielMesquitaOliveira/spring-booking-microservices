package com.bookingmicroservice.userservice.controller;

import com.bookingmicroservice.userservice.application.dto.CreateUserRequest;
import com.bookingmicroservice.userservice.infrastructure.persistence.entity.UserJpaEntity;
import com.bookingmicroservice.userservice.infrastructure.persistence.repository.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@DisplayName("UserController Integration Tests")
class UserControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/users - Should create user with valid data and return 201")
    void shouldCreateUserWithValidData() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
                "johndoe",
                "john.doe@example.com",
                "John Doe"
        );

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when username is blank")
    void shouldReturn400WhenUsernameIsBlank() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
                "",
                "john.doe@example.com",
                "John Doe"
        );

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when email is blank")
    void shouldReturn400WhenEmailIsBlank() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
                "johndoe",
                "",
                "John Doe"
        );

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when email format is invalid")
    void shouldReturn400WhenEmailFormatIsInvalid() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
                "johndoe",
                "invalid-email",
                "John Doe"
        );

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when fullName is blank")
    void shouldReturn400WhenFullNameIsBlank() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
                "johndoe",
                "john.doe@example.com",
                ""
        );

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return existing user")
    void shouldReturnExistingUser() throws Exception {
        // Given
        UserJpaEntity savedUser = userJpaRepository.save(
                new UserJpaEntity(null, "johndoe", "john.doe@example.com", "John Doe")
        );

        // When & Then
        mockMvc.perform(get("/api/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 404 when user not found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users - Should return list of all users")
    void shouldReturnAllUsers() throws Exception {
        // Given
        userJpaRepository.save(new UserJpaEntity(null, "user1", "user1@example.com", "User One"));
        userJpaRepository.save(new UserJpaEntity(null, "user2", "user2@example.com", "User Two"));
        userJpaRepository.save(new UserJpaEntity(null, "user3", "user3@example.com", "User Three"));

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[2].username").value("user3"));
    }

    @Test
    @DisplayName("GET /api/users - Should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsersExist() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
