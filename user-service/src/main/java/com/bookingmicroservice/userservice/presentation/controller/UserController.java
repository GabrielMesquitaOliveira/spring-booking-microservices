package com.bookingmicroservice.userservice.presentation.controller;

import com.bookingmicroservice.userservice.application.dto.CreateUserRequest;
import com.bookingmicroservice.userservice.application.dto.UserResponse;
import com.bookingmicroservice.userservice.application.usecase.CreateUserUseCase;
import com.bookingmicroservice.userservice.application.usecase.GetUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return createUserUseCase.execute(request);
    }
    
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return getUserUseCase.execute(id);
    }
    
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return getUserUseCase.executeAll();
    }
}
