package com.bookingmicroservice.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway routing configuration.
 * 
 * NOTE: YAML configuration did not work in Spring Boot 4.0.2 / Spring Cloud 2025.1.0.
 * Possible causes:
 * - Known bug in early release versions of Spring Cloud Gateway
 * - Incompatibility with spring-cloud-starter-gateway-server-webflux
 * - Configuration precedence issue
 * 
 * Solution: Programmatic configuration via RouteLocator Bean (official standard and more reliable)
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route to User Service
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.stripPrefix(1))  // Remove /api from path
                        .uri("lb://USER-SERVICE"))       // Load balance via Eureka
                
                // Route to Reservation Service
                .route("reservation-service", r -> r
                        .path("/api/reservations/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://RESERVATION-SERVICE"))
                .build();
    }
}
