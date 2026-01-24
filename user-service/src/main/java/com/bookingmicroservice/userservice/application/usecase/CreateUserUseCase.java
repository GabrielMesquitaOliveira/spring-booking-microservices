package com.bookingmicroservice.userservice.application.usecase;

import com.bookingmicroservice.userservice.application.dto.CreateUserRequest;
import com.bookingmicroservice.userservice.application.dto.UserResponse;
import com.bookingmicroservice.userservice.application.mapper.UserMapper;
import com.bookingmicroservice.userservice.domain.entity.User;
import com.bookingmicroservice.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Transactional
    public UserResponse execute(CreateUserRequest request) {
        User user = userMapper.toDomain(request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
