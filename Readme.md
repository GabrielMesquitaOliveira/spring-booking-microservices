# Spring Booking Microservices

🎓 **Educational Project** - Simple booking system with microservices architecture built for learning purposes as a school architecture project.

A lightweight demonstration of microservices patterns using Spring Boot and Spring Cloud.

## 📁 Project Structure

```
spring-booking-microservices/
├── README.md                  # This file
│
├── service-discovery/         # Service Discovery (Port 8761)
│   ├── pom.xml               # Independent Maven project
│   └── src/
│
├── api-gateway/               # API Gateway (Port 8080)
│   ├── pom.xml               # Independent Maven project
│   └── src/
│
├── user-service/              # User Service (Port 8081)
│   ├── pom.xml               # Independent Maven project
│   └── src/
│
└── reservation-service/       # Reservation Service (Port 8082)
    ├── pom.xml               # Independent Maven project
    └── src/
```

## 🚀 Technologies

- **Java 21**
- **Spring Boot 4.0.2**
- **Spring Cloud 2025.1.0**
- **H2 Database** (development)
- **PostgreSQL** (production)
- **Lombok** (boilerplate reduction)
- **MapStruct** (object-to-object mapping)
- **JWT** (authentication)
- **Swagger/OpenAPI** (documentation)
- **Eureka** (service discovery)
- **OpenFeign** (inter-service communication)
- **Resilience4j** (circuit breaker)

## 📋 Prerequisites

- Java 25+
- Maven 3.8+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🔧 Installation and Execution

### 1. Clone the repository
```bash
git clone <your-repository>
cd spring-booking-microservices
```

### 2. Build all services

#### Option A: Using the start script (builds automatically)
```bash
./start-all.sh
```
This script will:
- Build all services with `mvn clean install`
- Start services in the correct order
- Show logs location and service URLs

#### Option B: Manual build
Each service is now an independent Maven project. You can build all at once or individually:

```bash
# Build all services (from root)
for service in service-discovery api-gateway user-service reservation-service; do
  echo "Building $service..."
  (cd $service && mvn clean install)
done

# Or build individually
cd service-discovery && mvn clean install && cd ..
cd api-gateway && mvn clean install && cd ..
cd user-service && mvn clean install && cd ..
cd reservation-service && mvn clean install && cd ..
```

### 3. Run the services

**Important:** Services must start in this order:
1. service-discovery (Eureka)
2. api-gateway
3. user-service & reservation-service (can run in parallel)

#### Option A: Using the automated script (Recommended)
```bash
./start-all.sh
```

This script will:
- ✅ Build all services
- ✅ Start them in the correct order
- ✅ Wait appropriate time between services
- ✅ Create log files in `./logs/`
- ✅ Show all service URLs including API documentation

To stop all services:
```bash
./stop-all.sh
```

#### Option B: Manual startup (separate terminal for each)
```bash
# Terminal 1 - Eureka Server
cd service-discovery
mvn spring-boot:run

# Terminal 2 - API Gateway (wait ~30s after Eureka)
cd api-gateway
mvn spring-boot:run

# Terminal 3 - User Service
cd user-service
mvn spring-boot:run

# Terminal 4 - Reservation Service
cd reservation-service
mvn spring-boot:run
```

## 🌐 Service URLs

| Service | Port | URL | Description |
|---------|------|-----|-------------|
| Eureka Server | 8761 | http://localhost:8761 | Service Discovery Dashboard |
| API Gateway | 8080 | http://localhost:8080 | Single entry point for all APIs |
| User Service | 8081 | http://localhost:8081 | User management |
| Reservation Service | 8082 | http://localhost:8082 | Reservation management |

### 📖 API Documentation (Scalar)
Modern, interactive API documentation powered by Scalar (better than Swagger UI):

- **User Service:** http://localhost:8081/scalar/index.html
- **Reservation Service:** http://localhost:8082/scalar/index.html

**Features:**
- 🎨 Beautiful, modern UI
- ⚡ Fast and responsive
- 🔍 Better search and navigation
- 📱 Mobile-friendly
- 🌓 Dark mode support

### 🗄️ H2 Database Console
- **User Service:** http://localhost:8081/h2-console
- **Reservation Service:** http://localhost:8082/h2-console
  - JDBC URL: `jdbc:h2:mem:userdb` (for user-service) or `jdbc:h2:mem:reservationdb` (for reservation-service)
  - Username: `sa`
  - Password: (empty)

## 📡 Main Endpoints

### User Service (via Gateway: http://localhost:8080/api/users)
```
POST   /api/users/register     # Register user
POST   /api/users/login        # Login
GET    /api/users/{id}         # Get user by ID
GET    /api/users              # List all users
PUT    /api/users/{id}         # Update user
DELETE /api/users/{id}         # Delete user
```

### Reservation Service (via Gateway: http://localhost:8080/api/reservations)
```
POST   /api/reservations       # Create reservation
GET    /api/reservations/{id}  # Get reservation by ID
GET    /api/reservations       # List all reservations
PUT    /api/reservations/{id}  # Update reservation
DELETE /api/reservations/{id}  # Cancel reservation
```

## 🧪 Testing with cURL

### Register user
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@email.com",
    "password": "password123",
    "fullName": "John Silva",
    "phoneNumber": "+5511999999999"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john",
    "password": "password123"
  }'
```

### Create reservation (with token)
```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "resourceName": "Meeting Room A",
    "startDate": "2024-02-15T10:00:00",
    "endDate": "2024-02-15T12:00:00",
    "description": "Planning meeting"
  }'
```

## 🏗️ Architecture

```
                    ┌─────────────────┐
                    │  Eureka Server  │
                    │   (Port 8761)   │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
    ┌─────────▼────────┐    │    ┌────────▼─────────┐
    │   API Gateway    │    │    │   User Service   │
    │   (Port 8080)    │◄───┼───►│   (Port 8081)    │
    └─────────┬────────┘    │    └──────────────────┘
              │             │
              │    ┌────────▼──────────────┐
              └───►│ Reservation Service   │
                   │    (Port 8082)        │
                   └───────────────────────┘
```

## 📦 Dependencies by Module

### Eureka Server
- Spring Cloud Eureka Server
- Spring Boot Actuator

### API Gateway
- Spring Cloud Gateway
- Eureka Client
- Resilience4j

### User Service
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (jjwt)
- H2 Database
- Lombok
- MapStruct
- Swagger

### Reservation Service
- Spring Web
- Spring Data JPA
- OpenFeign (communication with User Service)
- H2 Database
- Lombok
- MapStruct
- Swagger
- Resilience4j

## 🔒 Security

The system uses JWT (JSON Web Tokens) for authentication:

1. User logs in at `/api/users/login`
2. Receives a JWT token
3. Uses the token in the `Authorization: Bearer TOKEN` header to access protected endpoints

## 🐛 Troubleshooting

### Error: "Connection refused" when trying to access service
- Make sure Eureka Server is running first
- Wait ~30 seconds for services to register

### Error: "Port already in use"
- Check if there's another application running on the port
- Change the port in the service's `application.yml`

### Lombok/MapStruct compilation error
- Make sure you have the Lombok plugin installed in your IDE
- Run `mvn clean install` at the root

## 📝 Next Steps

- [ ] Add unit and integration tests
- [ ] Implement PostgreSQL database for production
- [ ] Add Docker and Docker Compose
- [ ] Implement CI/CD
- [ ] Add centralized logging
- [ ] Implement messaging (Kafka/RabbitMQ)

## 🎯 Learning Objectives

This project demonstrates:
- **Microservices Architecture**: Service decomposition and independent deployment
- **Service Discovery**: Dynamic service registration with Eureka
- **API Gateway Pattern**: Single entry point for all services
- **Inter-Service Communication**: Using OpenFeign for REST calls
- **Authentication**: JWT-based security
- **API Documentation**: Swagger/OpenAPI integration

## 📝 Note

This is a **simplified educational project** designed for learning microservices concepts. It is not production-ready and intentionally keeps complexity minimal to focus on architectural patterns.

## 📄 License

This project is for educational purposes only.