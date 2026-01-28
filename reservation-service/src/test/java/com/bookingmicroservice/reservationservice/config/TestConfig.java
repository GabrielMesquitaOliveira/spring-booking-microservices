package com.bookingmicroservice.reservationservice.config;

import com.bookingmicroservice.reservationservice.infrastructure.client.UserServiceClient;
import com.bookingmicroservice.reservationservice.infrastructure.client.dto.UserResponse;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {

    @Bean
    public UserServiceClient mockUserServiceClient() {
        UserServiceClient mockClient = Mockito.mock(UserServiceClient.class);
        UserResponse mockResponse = new UserResponse(1L, "johndoe", "john@example.com", "John Doe");
        when(mockClient.getUserById(anyLong())).thenReturn(mockResponse);
        return mockClient;
    }
}
