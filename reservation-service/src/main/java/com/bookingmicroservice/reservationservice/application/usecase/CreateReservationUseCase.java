package com.bookingmicroservice.reservationservice.application.usecase;

import com.bookingmicroservice.reservationservice.application.dto.CreateReservationRequest;
import com.bookingmicroservice.reservationservice.application.dto.ReservationResponse;
import com.bookingmicroservice.reservationservice.application.mapper.ReservationMapper;
import com.bookingmicroservice.reservationservice.domain.entity.Reservation;
import com.bookingmicroservice.reservationservice.domain.repository.ReservationRepository;
import com.bookingmicroservice.reservationservice.infrastructure.client.UserServiceClient;
import com.bookingmicroservice.reservationservice.infrastructure.client.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateReservationUseCase {
    
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserServiceClient userServiceClient;
    
    @Transactional
    public ReservationResponse execute(CreateReservationRequest request) {
        // Validate user exists
        UserResponse user = userServiceClient.getUserById(request.userId());
        
        // Create reservation with PENDING status
        Reservation reservation = reservationMapper.toDomain(request);
        Reservation pendingReservation = new Reservation(
            null,
            reservation.userId(),
            reservation.resourceName(),
            reservation.startDate(),
            reservation.endDate(),
            "PENDING"
        );
        
        Reservation savedReservation = reservationRepository.save(pendingReservation);
        
        return reservationMapper.toResponseWithUsername(savedReservation, user.username());
    }
}
