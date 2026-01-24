package com.bookingmicroservice.userservice.infrastructure.persistence.repository;

import com.bookingmicroservice.userservice.application.mapper.UserMapper;
import com.bookingmicroservice.userservice.domain.entity.User;
import com.bookingmicroservice.userservice.domain.repository.UserRepository;
import com.bookingmicroservice.userservice.infrastructure.persistence.entity.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;
    
    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
            .map(userMapper::toDomain);
    }
    
    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = userMapper.toJpaEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(jpaEntity);
        return userMapper.toDomain(savedEntity);
    }
    
    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
            .map(userMapper::toDomain)
            .toList();
    }
}
