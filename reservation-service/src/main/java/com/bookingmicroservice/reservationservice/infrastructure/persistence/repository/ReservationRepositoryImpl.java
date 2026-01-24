package com.bookingmicroservice.reservationservice.infrastructure.persistence.repository;

import com.bookingmicroservice.reservationservice.application.mapper.ReservationMapper;
import com.bookingmicroservice.reservationservice.domain.entity.Reservation;
import com.bookingmicroservice.reservationservice.domain.repository.ReservationRepository;
import com.bookingmicroservice.reservationservice.infrastructure.persistence.entity.ReservationJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    
    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationMapper reservationMapper;
    
    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id)
            .map(reservationMapper::toDomain);
    }
    
    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity jpaEntity = reservationMapper.toJpaEntity(reservation);
        ReservationJpaEntity savedEntity = reservationJpaRepository.save(jpaEntity);
        return reservationMapper.toDomain(savedEntity);
    }
    
    @Override
    public List<Reservation> findAll() {
        return reservationJpaRepository.findAll().stream()
            .map(reservationMapper::toDomain)
            .toList();
    }
    
    @Override
    public List<Reservation> findByUserId(Long userId) {
        return reservationJpaRepository.findByUserId(userId).stream()
            .map(reservationMapper::toDomain)
            .toList();
    }
}
