# Spring Booking Microservices

Sistema de Reservas com Arquitetura de Microsserviços usando Spring Boot e Spring Cloud.

## 📁 Estrutura do Projeto

```
spring-booking-microservices/
├── pom.xml                    # POM pai (agregador)
├── README.md                  # Este arquivo
│
├── eureka-server/             # Service Discovery (Porta 8761)
│   ├── pom.xml
│   └── src/
│
├── api-gateway/               # API Gateway (Porta 8080)
│   ├── pom.xml
│   └── src/
│
├── user-service/              # Serviço de Usuários (Porta 8081)
│   ├── pom.xml
│   └── src/
│
└── reservation-service/       # Serviço de Reservas (Porta 8082)
    ├── pom.xml
    └── src/
```

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 4.0.2**
- **Spring Cloud 2025.1.0**
- **H2 Database** (desenvolvimento)
- **PostgreSQL** (produção)
- **Lombok** (redução de boilerplate)
- **MapStruct** (mapeamento objeto-objeto)
- **JWT** (autenticação)
- **Swagger/OpenAPI** (documentação)
- **Eureka** (service discovery)
- **OpenFeign** (comunicação entre serviços)
- **Resilience4j** (circuit breaker)

## 📋 Pré-requisitos

- Java 21+
- Maven 3.8+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🔧 Instalação e Execução

### 1. Clonar o repositório
```bash
git clone <seu-repositorio>
cd spring-booking-microservices
```

### 2. Compilar todos os módulos
```bash
mvn clean install
```

### 3. Executar os serviços (na ordem)

#### Opção A: Manualmente (terminal separado para cada)
```bash
# Terminal 1 - Eureka Server
cd eureka-server
mvn spring-boot:run

# Terminal 2 - API Gateway
cd api-gateway
mvn spring-boot:run

# Terminal 3 - User Service
cd user-service
mvn spring-boot:run

# Terminal 4 - Reservation Service
cd reservation-service
mvn spring-boot:run
```

#### Opção B: Usando Maven da raiz
```bash
# Eureka
mvn spring-boot:run -pl eureka-server

# Gateway
mvn spring-boot:run -pl api-gateway

# User Service
mvn spring-boot:run -pl user-service

# Reservation Service
mvn spring-boot:run -pl reservation-service
```

## 🌐 URLs dos Serviços

| Serviço | Porta | URL | Descrição |
|---------|-------|-----|-----------|
| Eureka Server | 8761 | http://localhost:8761 | Dashboard do Eureka |
| API Gateway | 8080 | http://localhost:8080 | Ponto de entrada único |
| User Service | 8081 | http://localhost:8081 | Gestão de usuários |
| Reservation Service | 8082 | http://localhost:8082 | Gestão de reservas |

### Swagger/OpenAPI
- User Service: http://localhost:8081/swagger-ui.html
- Reservation Service: http://localhost:8082/swagger-ui.html

### H2 Console
- User Service: http://localhost:8081/h2-console
- Reservation Service: http://localhost:8082/h2-console

## 📡 Endpoints Principais

### User Service (via Gateway: http://localhost:8080/api/users)
```
POST   /api/users/register     # Registrar usuário
POST   /api/users/login        # Login
GET    /api/users/{id}         # Buscar usuário por ID
GET    /api/users              # Listar todos usuários
PUT    /api/users/{id}         # Atualizar usuário
DELETE /api/users/{id}         # Deletar usuário
```

### Reservation Service (via Gateway: http://localhost:8080/api/reservations)
```
POST   /api/reservations       # Criar reserva
GET    /api/reservations/{id}  # Buscar reserva por ID
GET    /api/reservations       # Listar todas reservas
PUT    /api/reservations/{id}  # Atualizar reserva
DELETE /api/reservations/{id}  # Cancelar reserva
```

## 🧪 Testando com cURL

### Registrar usuário
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao",
    "email": "joao@email.com",
    "password": "senha123",
    "fullName": "João Silva",
    "phoneNumber": "+5511999999999"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "joao",
    "password": "senha123"
  }'
```

### Criar reserva (com token)
```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "resourceName": "Sala de Reunião A",
    "startDate": "2024-02-15T10:00:00",
    "endDate": "2024-02-15T12:00:00",
    "description": "Reunião de planejamento"
  }'
```

## 🏗️ Arquitetura

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

## 📦 Dependências por Módulo

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
- OpenFeign (comunicação com User Service)
- H2 Database
- Lombok
- MapStruct
- Swagger
- Resilience4j

## 🔒 Segurança

O sistema utiliza JWT (JSON Web Tokens) para autenticação:

1. Usuário faz login em `/api/users/login`
2. Recebe um token JWT
3. Usa o token no header `Authorization: Bearer TOKEN` para acessar endpoints protegidos

## 🐛 Troubleshooting

### Erro: "Connection refused" ao tentar acessar serviço
- Verifique se o Eureka Server está rodando primeiro
- Aguarde ~30 segundos para os serviços se registrarem

### Erro: "Port already in use"
- Verifique se já existe outra aplicação rodando na porta
- Mude a porta no `application.yml` do serviço

### Erro de compilação com Lombok/MapStruct
- Certifique-se de ter o plugin do Lombok instalado na IDE
- Execute `mvn clean install` na raiz

## 📝 Próximos Passos

- [ ] Adicionar testes unitários e de integração
- [ ] Implementar banco PostgreSQL para produção
- [ ] Adicionar Docker e Docker Compose
- [ ] Implementar CI/CD
- [ ] Adicionar logging centralizado
- [ ] Implementar mensageria (Kafka/RabbitMQ)

## 👥 Autores

Seu Nome - Trabalho de Curso

## 📄 Licença

Este projeto é para fins educacionais.