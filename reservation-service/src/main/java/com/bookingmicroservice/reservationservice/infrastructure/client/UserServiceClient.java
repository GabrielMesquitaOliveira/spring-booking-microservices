package com.bookingmicroservice.reservationservice.infrastructure.client;

import com.bookingmicroservice.reservationservice.infrastructure.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}
