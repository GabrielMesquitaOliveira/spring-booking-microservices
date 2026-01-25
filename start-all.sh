#!/bin/bash

echo "🚀 Starting all microservices..."

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 1. Eureka Server (espera iniciar)
echo -e "${BLUE}Starting Eureka Server...${NC}"
cd service-discovery
mvn spring-boot:run > ../logs/eureka.log 2>&1 &
EUREKA_PID=$!
cd ..
echo -e "${GREEN}✓ Eureka Server started (PID: $EUREKA_PID)${NC}"
sleep 20  # Aguarda Eureka inicializar

# 2. API Gateway
echo -e "${BLUE}Starting API Gateway...${NC}"
cd api-gateway
mvn spring-boot:run > ../logs/gateway.log 2>&1 &
GATEWAY_PID=$!
cd ..
echo -e "${GREEN}✓ API Gateway started (PID: $GATEWAY_PID)${NC}"
sleep 10

# 3. User Service
echo -e "${BLUE}Starting User Service...${NC}"
cd user-service
mvn spring-boot:run > ../logs/user.log 2>&1 &
USER_PID=$!
cd ..
echo -e "${GREEN}✓ User Service started (PID: $USER_PID)${NC}"
sleep 10

# 4. Reservation Service
echo -e "${BLUE}Starting Reservation Service...${NC}"
cd reservation-service
mvn spring-boot:run > ../logs/reservation.log 2>&1 &
RESERVATION_PID=$!
cd ..
echo -e "${GREEN}✓ Reservation Service started (PID: $RESERVATION_PID)${NC}"

echo ""
echo "========================================"
echo "✅ All services started!"
echo "========================================"
echo ""
echo "📊 URLs:"
echo "  - Eureka:      http://localhost:8761"
echo "  - Gateway:     http://localhost:8080"
echo "  - User:        http://localhost:8081"
echo "  - Reservation: http://localhost:8082"
echo ""
echo "📝 Logs in: ./logs/"
echo ""
echo "To stop all services, run: ./stop-all.sh"
echo ""

# Salvar PIDs para parar depois
echo $EUREKA_PID > .pids/eureka.pid
echo $GATEWAY_PID > .pids/gateway.pid
echo $USER_PID > .pids/user.pid
echo $RESERVATION_PID > .pids/reservation.pid