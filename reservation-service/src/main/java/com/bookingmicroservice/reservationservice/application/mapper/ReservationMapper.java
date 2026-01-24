package com.bookingmicroservice.reservationservice.application.mapper;

import com.bookingmicroservice.reservationservice.application.dto.CreateReservationRequest;
import com.bookingmicroservice.reservationservice.application.dto.ReservationResponse;
import com.bookingmicroservice.reservationservice.domain.entity.Reservation;
import com.bookingmicroservice.reservationservice.infrastructure.persistence.entity.ReservationJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    
    @Mapping(target = "username", ignore = true)
    ReservationResponse toResponse(Reservation reservation);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Reservation toDomain(CreateReservationRequest request);
    
    Reservation toDomain(ReservationJpaEntity jpaEntity);
    
    ReservationJpaEntity toJpaEntity(Reservation reservation);
    
    default ReservationResponse toResponseWithUsername(Reservation reservation, String username) {
        return new ReservationResponse(
            reservation.id(),
            reservation.userId(),
            username,
            reservation.resourceName(),
            reservation.startDate(),
            reservation.endDate(),
            reservation.status()
        );
    }
}
