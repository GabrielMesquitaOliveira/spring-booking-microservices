package com.bookingmicroservice.userservice.application.usecase;

import com.bookingmicroservice.userservice.application.dto.UserResponse;
import com.bookingmicroservice.userservice.application.mapper.UserMapper;
import com.bookingmicroservice.userservice.domain.entity.User;
import com.bookingmicroservice.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserUseCase {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Transactional(readOnly = true)
    public UserResponse execute(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new com.bookingmicroservice.userservice.domain.exception.UserNotFoundException(id));
        return userMapper.toResponse(user);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> executeAll() {
        return userRepository.findAll().stream()
            .map(userMapper::toResponse)
            .toList();
    }
}
