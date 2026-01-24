package com.bookingmicroservice.userservice.application.mapper;

import com.bookingmicroservice.userservice.application.dto.CreateUserRequest;
import com.bookingmicroservice.userservice.application.dto.UserResponse;
import com.bookingmicroservice.userservice.domain.entity.User;
import com.bookingmicroservice.userservice.infrastructure.persistence.entity.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
    
    @Mapping(target = "id", ignore = true)
    User toDomain(CreateUserRequest request);
    
    User toDomain(UserJpaEntity jpaEntity);
    UserJpaEntity toJpaEntity(User user);
}
