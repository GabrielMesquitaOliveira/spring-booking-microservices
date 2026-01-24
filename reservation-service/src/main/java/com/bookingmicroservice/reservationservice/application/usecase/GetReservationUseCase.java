package com.bookingmicroservice.reservationservice.application.usecase;

import com.bookingmicroservice.reservationservice.application.dto.ReservationResponse;
import com.bookingmicroservice.reservationservice.application.mapper.ReservationMapper;
import com.bookingmicroservice.reservationservice.domain.entity.Reservation;
import com.bookingmicroservice.reservationservice.domain.repository.ReservationRepository;
import com.bookingmicroservice.reservationservice.infrastructure.client.UserServiceClient;
import com.bookingmicroservice.reservationservice.infrastructure.client.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetReservationUseCase {
    
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserServiceClient userServiceClient;
    
    @Transactional(readOnly = true)
    public ReservationResponse execute(Long id) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        
        UserResponse user = userServiceClient.getUserById(reservation.userId());
        
        return reservationMapper.toResponseWithUsername(reservation, user.username());
    }
    
    @Transactional(readOnly = true)
    public List<ReservationResponse> executeAll() {
        return reservationRepository.findAll().stream()
            .map(reservation -> {
                UserResponse user = userServiceClient.getUserById(reservation.userId());
                return reservationMapper.toResponseWithUsername(reservation, user.username());
            })
            .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ReservationResponse> executeByUserId(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
            .map(reservation -> {
                UserResponse user = userServiceClient.getUserById(reservation.userId());
                return reservationMapper.toResponseWithUsername(reservation, user.username());
            })
            .toList();
    }
}
