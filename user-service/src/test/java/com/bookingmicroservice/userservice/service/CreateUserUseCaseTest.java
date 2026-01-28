package com.bookingmicroservice.userservice.service;

import com.bookingmicroservice.userservice.application.dto.CreateUserRequest;
import com.bookingmicroservice.userservice.application.dto.UserResponse;
import com.bookingmicroservice.userservice.application.mapper.UserMapper;
import com.bookingmicroservice.userservice.application.usecase.CreateUserUseCase;
import com.bookingmicroservice.userservice.domain.entity.User;
import com.bookingmicroservice.userservice.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase Unit Tests")
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private CreateUserRequest validRequest;
    private User user;
    private User savedUser;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        validRequest = new CreateUserRequest(
                "johndoe",
                "john.doe@example.com",
                "John Doe"
        );

        user = new User(null, "johndoe", "john.doe@example.com", "John Doe");
        savedUser = new User(1L, "johndoe", "john.doe@example.com", "John Doe");
        userResponse = new UserResponse(1L, "johndoe", "john.doe@example.com", "John Doe");
    }

    @Test
    @DisplayName("Should successfully create user with valid data")
    void shouldCreateUserWithValidData() {
        // Given
        when(userMapper.toDomain(validRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(userResponse);

        // When
        UserResponse result = createUserUseCase.execute(validRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("johndoe");
        assertThat(result.email()).isEqualTo("john.doe@example.com");
        assertThat(result.fullName()).isEqualTo("John Doe");

        verify(userMapper).toDomain(validRequest);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    @DisplayName("Should call mapper and repository in correct order")
    void shouldCallDependenciesInCorrectOrder() {
        // Given
        when(userMapper.toDomain(any(CreateUserRequest.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        // When
        createUserUseCase.execute(validRequest);

        // Then
        var inOrder = inOrder(userMapper, userRepository, userMapper);
        inOrder.verify(userMapper).toDomain(validRequest);
        inOrder.verify(userRepository).save(user);
        inOrder.verify(userMapper).toResponse(savedUser);
    }

    @Test
    @DisplayName("Should handle repository save correctly")
    void shouldHandleRepositorySave() {
        // Given
        when(userMapper.toDomain(validRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(userResponse);

        // When
        UserResponse result = createUserUseCase.execute(validRequest);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).save(user);
    }
}
