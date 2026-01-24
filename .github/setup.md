# Copilot Prompt: Spring Boot Microservices Minimal Setup with Clean Architecture

## Context
I have a Maven monorepo project called `spring-booking-microservices` with 4 modules:
- `eureka-server` (port 8761)
- `api-gateway` (port 8080)
- `user-service` (port 8081)
- `reservation-service` (port 8082)

All POMs are configured. Now I need the minimal Java code to test microservices communication.

## Requirements

### Clean Architecture Structure
Each service (except Eureka and Gateway) must follow Clean Architecture with these layers:
```
src/main/java/com/bookingmicroservice/{service}/
├── domain/
│   ├── entity/           # Core business entities
│   └── repository/       # Repository interfaces (ports)
├── application/
│   ├── dto/              # Request/Response DTOs
│   ├── usecase/          # Business logic (use cases)
│   └── mapper/           # Entity <-> DTO mappers (MapStruct)
├── infrastructure/
│   ├── persistence/      # JPA implementations
│   │   ├── entity/       # JPA entities
│   │   └── repository/   # JPA repository implementations
│   └── client/           # Feign clients (for external calls)
└── presentation/
    └── controller/       # REST controllers
```

---

## Task 1: Eureka Server Setup

### File: `eureka-server/src/main/java/com/bookingmicroservice/eureka/EurekaServerApplication.java`
```
Create Spring Boot main class with:
- @SpringBootApplication
- @EnableEurekaServer
- Standard main method
```

### File: `eureka-server/src/main/resources/application.yml`
```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    wait-time-in-ms-when-sync-empty: 0
```

---

## Task 2: API Gateway Setup

### File: `api-gateway/src/main/java/com/bookingmicroservice/gateway/ApiGatewayApplication.java`
```
Create Spring Boot main class with:
- @SpringBootApplication
- Standard main method
```

### File: `api-gateway/src/main/resources/application.yml`
```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
            
        - id: reservation-service
          uri: lb://reservation-service
          predicates:
            - Path=/api/reservations/**
          filters:
            - StripPrefix=1

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

## Task 3: User Service Setup (Clean Architecture)

### Main Application
**File:** `user-service/src/main/java/com/bookingmicroservice/userservice/UserServiceApplication.java`
```
Create with:
- @SpringBootApplication
- @EnableDiscoveryClient
```

### Domain Layer

**File:** `domain/entity/User.java`
```java
Create domain entity (NOT JPA) with:
- Long id
- String username
- String email
- String fullName
- Use records or simple class
- NO JPA annotations
```

**File:** `domain/repository/UserRepository.java`
```java
Create repository interface (port) with:
- Optional<User> findById(Long id)
- User save(User user)
- List<User> findAll()
```

### Application Layer

**File:** `application/dto/UserResponse.java`
```java
Create DTO with:
- Long id
- String username
- String email
- String fullName
```

**File:** `application/dto/CreateUserRequest.java`
```java
Create DTO with:
- @NotBlank String username
- @Email String email
- @NotBlank String fullName
- Use Jakarta validation annotations
```

**File:** `application/usecase/CreateUserUseCase.java`
```java
@Service
Create use case that:
- Receives CreateUserRequest
- Converts to domain User using mapper
- Calls repository.save()
- Returns UserResponse
```

**File:** `application/usecase/GetUserUseCase.java`
```java
@Service
Create use case that:
- Receives Long id
- Calls repository.findById()
- Returns UserResponse
- Throws exception if not found
```

**File:** `application/mapper/UserMapper.java`
```java
@Mapper(componentModel = "spring")
Create MapStruct interface with:
- UserResponse toResponse(User user)
- User toDomain(CreateUserRequest request)
- User toDomain(UserJpaEntity jpaEntity)
- UserJpaEntity toJpaEntity(User user)
```

### Infrastructure Layer

**File:** `infrastructure/persistence/entity/UserJpaEntity.java`
```java
@Entity
@Table(name = "users")
Create JPA entity with:
- @Id @GeneratedValue Long id
- @Column String username
- @Column String email
- @Column String fullName
- Use Lombok annotations
```

**File:** `infrastructure/persistence/repository/UserJpaRepository.java`
```java
Create interface extending JpaRepository<UserJpaEntity, Long>
```

**File:** `infrastructure/persistence/repository/UserRepositoryImpl.java`
```java
@Repository
Create adapter that:
- Implements domain UserRepository interface
- Uses UserJpaRepository internally
- Converts between domain User and UserJpaEntity
```

### Presentation Layer

**File:** `presentation/controller/UserController.java`
```java
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
Create controller with:
- POST / -> CreateUserUseCase
- GET /{id} -> GetUserUseCase
- GET / -> list all users
Use use cases, NOT repositories directly
```

### Configuration

**File:** `user-service/src/main/resources/application.yml`
```yaml
server:
  port: 8081

spring:
  application:
    name: user-service
  datasource:
    url: jdbc:h2:mem:userdb
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

## Task 4: Reservation Service Setup (Clean Architecture)

### Main Application
**File:** `reservation-service/src/main/java/com/bookingmicroservice/reservationservice/ReservationServiceApplication.java`
```
Create with:
- @SpringBootApplication
- @EnableDiscoveryClient
- @EnableFeignClients
```

### Domain Layer

**File:** `domain/entity/Reservation.java`
```java
Create domain entity with:
- Long id
- Long userId
- String resourceName
- LocalDateTime startDate
- LocalDateTime endDate
- String status (PENDING, CONFIRMED, CANCELLED)
- NO JPA annotations
```

**File:** `domain/repository/ReservationRepository.java`
```java
Create repository interface with:
- Optional<Reservation> findById(Long id)
- Reservation save(Reservation reservation)
- List<Reservation> findAll()
- List<Reservation> findByUserId(Long userId)
```

### Application Layer

**File:** `application/dto/ReservationResponse.java`
```java
Create DTO with:
- Long id
- Long userId
- String username (from User Service)
- String resourceName
- LocalDateTime startDate
- LocalDateTime endDate
- String status
```

**File:** `application/dto/CreateReservationRequest.java`
```java
Create DTO with:
- @NotNull Long userId
- @NotBlank String resourceName
- @Future LocalDateTime startDate
- @Future LocalDateTime endDate
```

**File:** `application/usecase/CreateReservationUseCase.java`
```java
@Service
Create use case that:
1. Receives CreateReservationRequest
2. Calls UserServiceClient to validate userId exists
3. Creates domain Reservation with status PENDING
4. Saves via repository
5. Returns ReservationResponse with username from UserService
```

**File:** `application/usecase/GetReservationUseCase.java`
```java
@Service
Create use case that:
1. Receives Long id
2. Finds reservation
3. Calls UserServiceClient to get username
4. Returns ReservationResponse with username
```

**File:** `application/mapper/ReservationMapper.java`
```java
@Mapper(componentModel = "spring")
Create MapStruct interface for conversions
```

### Infrastructure Layer

**File:** `infrastructure/client/UserServiceClient.java`
```java
@FeignClient(name = "user-service")
Create Feign client with:
- @GetMapping("/users/{id}") to get user
- Returns UserResponse DTO
```

**File:** `infrastructure/client/dto/UserResponse.java`
```java
Create DTO matching User Service response (copy or share)
```

**File:** `infrastructure/persistence/entity/ReservationJpaEntity.java`
```java
@Entity
@Table(name = "reservations")
Create JPA entity with all fields
```

**File:** `infrastructure/persistence/repository/ReservationJpaRepository.java`
```java
Create interface extending JpaRepository
Add: List<ReservationJpaEntity> findByUserId(Long userId)
```

**File:** `infrastructure/persistence/repository/ReservationRepositoryImpl.java`
```java
@Repository
Create adapter implementing domain repository
```

### Presentation Layer

**File:** `presentation/controller/ReservationController.java`
```java
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
Create controller with:
- POST / -> CreateReservationUseCase
- GET /{id} -> GetReservationUseCase  
- GET / -> list all
- GET /user/{userId} -> list by user
```

### Configuration

**File:** `reservation-service/src/main/resources/application.yml`
```yaml
server:
  port: 8082

spring:
  application:
    name: reservation-service
  datasource:
    url: jdbc:h2:mem:reservationdb
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
```

---

## Success Criteria

### Test Flow:
1. Start Eureka Server → http://localhost:8761
2. Start API Gateway → port 8080
3. Start User Service → port 8081
4. Start Reservation Service → port 8082
5. All services visible in Eureka dashboard

### Test Commands:
```bash
# 1. Create a user via Gateway
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@test.com","fullName":"John Doe"}'

# Expected: Returns user with id=1

# 2. Get user via Gateway
curl http://localhost:8080/api/users/1

# Expected: Returns user details

# 3. Create reservation via Gateway
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "userId":1,
    "resourceName":"Meeting Room A",
    "startDate":"2024-03-01T10:00:00",
    "endDate":"2024-03-01T12:00:00"
  }'

# Expected: Returns reservation WITH username "John Doe" 
# (proving Feign client communication works)

# 4. Get reservation via Gateway
curl http://localhost:8080/api/reservations/1

# Expected: Returns reservation with user details
```

---

## Important Notes

1. **Clean Architecture Principles:**
   - Domain layer has NO framework dependencies
   - Use cases contain business logic
   - Controllers are thin, just call use cases
   - Infrastructure adapts external systems to domain interfaces

2. **Dependency Direction:**
   - Presentation → Application → Domain
   - Infrastructure → Domain (implements interfaces)
   - NO reverse dependencies

3. **MapStruct:**
   - Must be configured in maven-compiler-plugin
   - Already configured in parent POM

4. **Error Handling:**
   - Create simple @ControllerAdvice for exceptions
   - Return proper HTTP status codes

5. **Lombok:**
   - Use @Data, @Builder, @RequiredArgsConstructor
   - Avoid on JPA entities (use @Getter @Setter)

---

## Deliverables

Generate ALL files mentioned above with:
- Proper package structure
- Complete implementations (no TODOs)
- Working code ready to test
- Following Clean Architecture principles strictly

This is a MINIMAL setup just to prove microservices communicate correctly via Eureka and Gateway, with proper architectural separation.