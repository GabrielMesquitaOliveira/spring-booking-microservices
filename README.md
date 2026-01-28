# Room Reservation System - Microservices Architecture

**Final Project – Introduction to Software Architecture**

## 👥 Team Members

- Gabriel Mesquita Oliveira
---

## 📋 1. Problem Description

Companies and institutions face challenges managing shared spaces (meeting rooms, laboratories, auditoriums). Common problems include:

- **Scheduling conflicts**: Multiple reservations for the same time slot
- **Lack of control**: No record of who reserved and when
- **Inefficiency**: Manual processes prone to errors
- **Resource waste**: Idle rooms due to lack of visibility

### Real-World Context
This system solves a problem found in corporate and academic environments where there's a need to:
- Manage reservations for multiple resources
- Ensure no time slot overlaps
- Maintain records of users and their reservations
- Apply specific business rules (business hours, minimum/maximum duration)

---

## 🎯 2. System Objectives

Develop a **reservation management system** that:

### Main Features
1. **User Management**
   - User registration and authentication
   - Access control via JWT
   - User profile maintenance

2. **Reservation Management**
   - Create reservations with business rule validation
   - Query reservations by user or period
   - Check slot availability
   - Cancel/update reservations

3. **Implemented Business Rules**
   - ✅ Reservations must be exactly 30 minutes
   - ✅ Allowed times only at :00 or :30 minutes
   - ✅ Operation only during business hours (8 AM to 6 PM)
   - ✅ No overlapping reservations for the same resource
   - ✅ User must exist before creating reservation

---

## 🏛️ 3. Architectural Style Adopted

### **Microservices Architecture**

The system was designed using the **microservices** pattern, where the application is decomposed into independent services, each responsible for a specific business domain.

#### Implemented Microservices

```
┌─────────────────────────────────────────────────────────────┐
│                    EUREKA SERVER (8761)                      │
│                    Service Discovery                         │
└──────────────────────────┬──────────────────────────────────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
           ▼               ▼               ▼
    ┌──────────┐    ┌──────────┐   ┌─────────────┐
    │   API    │    │   USER   │   │ RESERVATION │
    │ GATEWAY  │◄──►│ SERVICE  │◄─►│   SERVICE   │
    │  (8080)  │    │  (8081)  │   │   (8082)    │
    └──────────┘    └──────────┘   └─────────────┘
         │                │               │
         │                │               │
         ▼                ▼               ▼
    [Clients]        [H2 DB]         [H2 DB]
```

#### Architecture Components

1. **Service Discovery (Eureka Server - Port 8761)**
   - Dynamic service registration
   - Automatic service discovery
   - Service health checking

2. **API Gateway (Port 8080)**
   - Single entry point
   - Intelligent routing
   - Load balancing
   - Circuit breaker (Resilience4j)

3. **User Service (Port 8081)**
   - User management
   - Authentication and authorization (JWT)
   - User CRUD operations
   - Independent database (H2)

4. **Reservation Service (Port 8082)**
   - Reservation management
   - Business rule validation
   - Communication with User Service (OpenFeign)
   - Independent database (H2)

---

## 📐 4. Architecture Diagram

### Macro View

```
                    PRESENTATION LAYER
                            │
                            ▼
                    ┌───────────────┐
                    │  API Gateway  │ ◄── Single Entry Point
                    └───────┬───────┘
                            │
            ┌───────────────┼───────────────┐
            │                               │
            ▼                               ▼
    ┌───────────────┐              ┌────────────────┐
    │ User Service  │              │ Reservation    │
    │               │◄─────────────┤ Service        │
    │ • Auth/JWT    │  OpenFeign   │ • Business     │
    │ • CRUD Users  │              │   Rules        │
    │               │              │ • Validations  │
    └───────┬───────┘              └────────┬───────┘
            │                               │
            │                               │
            ▼                               ▼
      ┌──────────┐                   ┌──────────┐
      │ H2 User  │                   │ H2 Res.  │
      │ Database │                   │ Database │
      └──────────┘                   └──────────┘

    ┌─────────────────────────────────────────────┐
    │         EUREKA SERVICE DISCOVERY            │
    │  (All services register and discover here)  │
    └─────────────────────────────────────────────┘
```

### Internal Service Architecture (Clean Architecture)

Both services follow a layered architecture inspired by Clean Architecture:

```
┌─────────────────────────────────────────────────┐
│           PRESENTATION LAYER                    │
│  • Controllers (REST endpoints)                 │
│  • Exception Handlers                           │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│           APPLICATION LAYER                     │
│  • Use Cases (orchestration)                    │
│  • DTOs (data transfer)                         │
│  • Mappers                                      │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│              DOMAIN LAYER                       │
│  • Entities (business objects)                  │
│  • Domain Services (business rules)             │
│  • Domain Exceptions                            │
│  • Repository Interfaces                        │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│         INFRASTRUCTURE LAYER                    │
│  • Repository Implementations (JPA)             │
│  • External Clients (Feign)                     │
│  • Configuration                                │
│  • Persistence Entities                         │
└─────────────────────────────────────────────────┘
```

---

## 💡 5. Justification of Architectural Decisions

### 5.1 Why Microservices?

**Decision**: Adopt microservices architecture instead of monolith

**Justification**:
1. **Domain Separation**: User and Reservation are distinct bounded contexts (DDD)
2. **Independent Scalability**: Reservation service may have more load than user service
3. **Parallel Development**: Teams can work independently
4. **Resilience**: Failure in one service doesn't bring down the entire system
5. **Technology**: Each service can evolve with its own stack

**Alternative Considered**: Monolithic Architecture
- ✅ Simpler for initial deployment
- ❌ Coupling between modules
- ❌ Difficulty scaling specific parts
- ❌ Risk of changes in one area affecting others

**Decision Impact**:
- ➕ Greater flexibility and maintainability
- ➕ Better failure isolation
- ➖ Increased operational complexity
- ➖ Need for service orchestration

### 5.2 Service Discovery with Eureka

**Decision**: Use Eureka Server for service discovery

**Justification**:
1. **Dynamic Service Discovery**: Services register automatically
2. **Load Balancing**: Automatic load distribution
3. **Health Checks**: Monitors service health
4. **No Hardcoding**: IPs and ports aren't fixed in code

**Alternative Considered**: Static endpoint configuration
- ❌ Requires restart for changes
- ❌ Difficult in dynamic environments (containers, cloud)

**Impact**:
- ➕ Greater flexibility in dynamic environments
- ➕ Automatic resilience
- ➖ Single point of failure (mitigated with Eureka clustering)

### 5.3 API Gateway Pattern

**Decision**: Implement API Gateway as single entry point

**Justification**:
1. **Client Simplification**: Single URL to access all services
2. **Centralized Routing**: Routing logic in one place
3. **Cross-Cutting Concerns**: Authentication, logging, rate limiting centralized
4. **Circuit Breaker**: Protection against cascading failures

**Alternative Considered**: Direct access to services
- ❌ Client needs to know multiple endpoints
- ❌ Cross-cutting logic scattered

**Impact**:
- ➕ Better client experience
- ➕ Single point for security policies
- ➖ Gateway can become bottleneck (mitigated with horizontal scaling)

### 5.4 Clean Architecture in Services

**Decision**: Organize code in layers (Presentation, Application, Domain, Infrastructure)

**Justification**:
1. **Separation of Responsibilities**: Each layer has a well-defined role
2. **Testability**: Facilitates unit and integration tests
3. **Framework Independence**: Domain doesn't depend on Spring
4. **Dependency Inversion**: Dependencies point inward

**Practical Example**:
```
Domain Layer (ReservationValidationService)
  → Defines pure business rules
  → Doesn't know HTTP, database, or frameworks

Application Layer (CreateReservationUseCase)
  → Orchestrates the flow
  → Uses domain services and repositories

Infrastructure Layer
  → Implements domain interfaces
  → Technical details (JPA, HTTP clients)
```

**Impact**:
- ➕ Cleaner and more testable code
- ➕ Isolated business rules
- ➖ More files and abstractions

### 5.5 Synchronous Communication with OpenFeign

**Decision**: Use OpenFeign for communication between Reservation ↔ User Service

**Justification**:
1. **Simplicity**: Declarative abstraction over HTTP
2. **Spring Integration**: Works natively with Eureka
3. **Maintainability**: Clear contract interface

**Alternative Considered**: Asynchronous messaging (Kafka, RabbitMQ)
- ✅ Better for events and high scale
- ❌ Unnecessary additional complexity for scope
- ❌ Eventual consistency can be problematic for user validation

**Impact**:
- ➕ Simple and direct for queries
- ➖ Temporal coupling (destination service must be UP)
- ➖ Increased latency

### 5.6 H2 Database

**Decision**: Use H2 in-memory for development

**Justification**:
1. **Simplicity**: Zero external configuration
2. **Fast**: Ideal for testing and development
3. **Portability**: Runs in any environment

**Production Alternative**: PostgreSQL (already configured, commented in pom.xml)

### 5.7 JWT for Authentication

**Decision**: Use JWT (JSON Web Tokens) for authentication

**Justification**:
1. **Stateless**: Doesn't require server session
2. **Scalable**: Works well with multiple instances
3. **Self-contained**: Token carries user information

---

## 🧪 6. Tests and Business Rule Validation

The system has **36 automated tests** validating all business rules:

### User Service - 13 tests
- **Unit Tests** (3 tests): CreateUserUseCase
- **Integration Tests** (9 tests): UserController
  - Required field validation
  - Email format validation
  - Exception handling (404 for user not found)

### Reservation Service - 23 tests
- **ReservationValidationService** (16 tests)
  - ✅ 30-minute duration (3 tests)
  - ✅ Slots at :00 or :30 (4 tests)
  - ✅ Business hours 8AM-6PM (4 tests)
  - ✅ Overlap detection (5 tests)
  
- **CreateReservationUseCase** (7 tests)
  - ✅ Successful creation
  - ✅ Existing user validation
  - ✅ All rules application

### Run Tests

```bash
# User Service
cd user-service
mvn test

# Reservation Service
cd reservation-service
mvn test
```

**Result**: ✅ 36/36 tests passing (100%)

---

## 🚀 7. Execution Instructions

### 7.1 Prerequisites
- **Java 25** or higher
- **Maven 3.8+**
- **Git**

### 7.2 Clone Repository

```bash
git clone <repository-url>
cd spring-booking-microservices
```

### 7.3 Run the System

#### Option A: Automated Script (Recommended)

```bash
./start-all.sh
```

The script will:
1. ✅ Compile all services (`mvn clean install`)
2. ✅ Start in correct order (Eureka → Gateway → Services)
3. ✅ Create logs in `./logs/`
4. ✅ Show access URLs

**To stop**:
```bash
./stop-all.sh
```

#### Option B: Manual Startup

**Important**: Start in this exact order:

```bash
# Terminal 1 - Eureka Server (wait ~30s)
cd service-discovery
mvn spring-boot:run

# Terminal 2 - API Gateway (wait for Eureka to be UP)
cd api-gateway
mvn spring-boot:run

# Terminal 3 - User Service
cd user-service
mvn spring-boot:run

# Terminal 4 - Reservation Service
cd reservation-service
mvn spring-boot:run
```

### 7.4 Access the System

| Service | URL | Description |
|---------|-----|-------------|
| **Eureka Dashboard** | http://localhost:8761 | View registered services |
| **API Gateway** | http://localhost:8080 | Entry point |
| **User Service API Docs** | http://localhost:8081/scalar/index.html | Interactive documentation |
| **Reservation Service API Docs** | http://localhost:8082/scalar/index.html | Interactive documentation |
| **H2 Console (User)** | http://localhost:8081/h2-console | Database |
| **H2 Console (Reservation)** | http://localhost:8082/h2-console | Database |

### 7.5 Test Endpoints

#### 1. Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "fullName": "John Silva"
  }'
```

#### 2. List Users
```bash
curl http://localhost:8080/api/users
```

#### 3. Create Reservation
```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "resourceName": "Room A",
    "startDate": "2026-02-15T10:00:00",
    "endDate": "2026-02-15T10:30:00"
  }'
```

#### 4. Check Available Slots
```bash
curl "http://localhost:8080/api/reservations/available-slots?date=2026-02-15&resourceName=Room%20A"
```

---

## 🛠️ 8. Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 25 | Main language |
| **Spring Boot** | 4.0.2 | Base framework |
| **Spring Cloud** | 2025.1.0 | Microservices |
| **Netflix Eureka** | - | Service Discovery |
| **Spring Cloud Gateway** | - | API Gateway |
| **OpenFeign** | - | HTTP Client |
| **H2 Database** | - | Database |
| **Lombok** | - | Boilerplate reduction |
| **MapStruct** | 1.6.3 | Object mapping |
| **JUnit 5** | - | Tests |
| **Mockito** | - | Mocks for testing |
| **Swagger/Scalar** | - | API Documentation |

---

## 📦 9. Project Structure

```
spring-booking-microservices/
│
├── README.md                           # This file
├── TEST_SUMMARY.md                     # Test summary
├── start-all.sh                        # Startup script
├── stop-all.sh                         # Stop script
│
├── service-discovery/                  # Eureka Server (8761)
│   ├── pom.xml
│   └── src/
│
├── api-gateway/                        # API Gateway (8080)
│   ├── pom.xml
│   └── src/
│
├── user-service/                       # User Service (8081)
│   ├── pom.xml
│   └── src/
│       ├── main/java/
│       │   └── com/bookingmicroservice/userservice/
│       │       ├── domain/                # Business logic
│       │       ├── application/           # Use cases & DTOs
│       │       ├── infrastructure/        # Technical details
│       │       └── presentation/          # Controllers
│       └── test/java/                     # 13 tests
│
└── reservation-service/                # Reservation Service (8082)
    ├── pom.xml
    └── src/
        ├── main/java/
        │   └── com/bookingmicroservice/reservationservice/
        │       ├── domain/                # Business rules
        │       ├── application/           # Use cases & DTOs
        │       ├── infrastructure/        # JPA, Feign
        │       └── presentation/          # Controllers
        └── test/java/                     # 23 tests
```

---

## 🎓 10. Architecture Concepts Demonstrated

This project exemplifies the following concepts studied in the module:

### 10.1 Architectural Patterns
- ✅ **Microservices**: Decomposition into independent services
- ✅ **Clean Architecture**: Layer separation with dependency inversion
- ✅ **Domain-Driven Design (DDD)**: Entities, Value Objects, Domain Services

### 10.2 Design Patterns
- ✅ **Repository Pattern**: Persistence abstraction
- ✅ **Use Case Pattern**: Business logic orchestration
- ✅ **DTO Pattern**: Data transfer between layers
- ✅ **Mapper Pattern**: Conversion between entities and DTOs
- ✅ **Exception Handler Pattern**: Centralized error handling

### 10.3 SOLID Principles
- ✅ **Single Responsibility**: Each class has a single responsibility
- ✅ **Open/Closed**: Extensible without modifying existing code
- ✅ **Liskov Substitution**: Well-defined interfaces
- ✅ **Interface Segregation**: Cohesive and specific interfaces
- ✅ **Dependency Inversion**: Dependencies point to abstractions

### 10.4 Architectural Qualities
- ✅ **Low Coupling**: Independent services
- ✅ **High Cohesion**: Related functionalities grouped
- ✅ **Testability**: 36 automated tests
- ✅ **Maintainability**: Clean and well-organized code
- ✅ **Scalability**: Services can scale independently

---

## 📊 11. Technical Decisions Summary

| Aspect | Decision | Justification |
|---------|---------|---------------|
| **Architecture** | Microservices | Domain separation, independent scalability |
| **Discovery** | Netflix Eureka | Dynamic discovery, automatic load balancing |
| **Gateway** | Spring Cloud Gateway | Single entry point, circuit breaker |
| **Communication** | OpenFeign (synchronous) | Simplicity, suitable for queries |
| **Database** | H2 (dev) | Zero configuration, portability |
| **Layers** | Clean Architecture | Testability, framework independence |
| **Authentication** | JWT | Stateless, scalable |
| **Tests** | JUnit 5 + Mockito | Business rule validation |

---

## 🎯 12. Results and Conclusion

### Achieved Objectives
✅ Functional system with well-defined business rules  
✅ Microservices architecture successfully implemented  
✅ Clear separation of responsibilities (Clean Architecture)  
✅ 36 automated tests validating all rules  
✅ Documented and justified architectural decisions  
✅ Executable and testable system  

### Learnings
- Practical understanding of microservices
- Importance of separation of responsibilities
- Trade-offs between different architectural styles
- Value of automated tests for validating business rules

### Possible Evolutions
- Implement persistent database (PostgreSQL)
- Add real authentication with Spring Security
- Implement messaging for asynchronous communication
- Containerize with Docker
- Add observability (logs, metrics, traces)

---

## 📚 13. References

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Spring Cloud Documentation**: https://spring.io/projects/spring-cloud
- **Clean Architecture**: Robert C. Martin
- **Building Microservices**: Sam Newman
- **Domain-Driven Design**: Eric Evans

---

## 📄 License

Educational project developed for the Introduction to Software Architecture course.

---

**Submission Date**: [Insert date]  
**Course**: [Insert course]  
**Institution**: [Insert institution]
