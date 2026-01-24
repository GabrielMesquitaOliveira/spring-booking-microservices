package com.bookingmicroservice.userservice.infrastructure.persistence.repository;

import com.bookingmicroservice.userservice.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
}
