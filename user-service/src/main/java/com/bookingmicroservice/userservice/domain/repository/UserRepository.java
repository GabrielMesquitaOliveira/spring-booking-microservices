package com.bookingmicroservice.userservice.domain.repository;

import com.bookingmicroservice.userservice.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    User save(User user);
    List<User> findAll();
}
