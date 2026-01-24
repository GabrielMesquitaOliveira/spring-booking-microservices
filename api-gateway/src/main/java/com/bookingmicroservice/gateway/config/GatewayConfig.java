package com.bookingmicroservice.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de rotas do Gateway.
 * 
 * NOTA: Configuração via YAML não funcionou no Spring Boot 4.0.2 / Spring Cloud 2025.1.0.
 * Possíveis causas:
 * - Bug conhecido em versões early release do Spring Cloud Gateway
 * - Incompatibilidade com spring-cloud-starter-gateway-server-webflux
 * - Problema de precedência de configuração
 * 
 * Solução: Configuração programática via RouteLocator Bean (padrão oficial e mais confiável)
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Rota para User Service
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.stripPrefix(1))  // Remove /api do path
                        .uri("lb://USER-SERVICE"))       // Load balance via Eureka
                
                // Rota para Reservation Service
                .route("reservation-service", r -> r
                        .path("/api/reservations/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://RESERVATION-SERVICE"))
                .build();
    }
}
