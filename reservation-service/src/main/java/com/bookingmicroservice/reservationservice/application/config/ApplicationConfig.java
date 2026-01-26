package com.bookingmicroservice.reservationservice.application.config;

import com.bookingmicroservice.reservationservice.domain.service.ReservationValidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    
    @Bean
    public ReservationValidationService reservationValidationService() {
        return new ReservationValidationService();
    }
} 
