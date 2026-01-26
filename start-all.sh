#!/bin/bash

echo "🚀 Starting all microservices..."

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Criar pasta de logs e PIDs
mkdir -p logs .pids

# Build todos os serviços primeiro
echo -e "${YELLOW}📦 Building all services...${NC}"
echo ""

for service in service-discovery api-gateway user-service reservation-service; do
    echo -e "${BLUE}Building $service...${NC}"
    (cd $service && mvn clean install -q -DskipTests) &
done

# Aguarda todos os builds terminarem
wait
echo ""
echo -e "${GREEN}✓ All services built successfully${NC}"
echo ""

# 1. Eureka Server (espera iniciar)
echo -e "${BLUE}Starting Eureka Server...${NC}"
cd service-discovery
mvn spring-boot:run > ../logs/eureka.log 2>&1 &
EUREKA_PID=$!
cd ..
echo -e "${GREEN}✓ Eureka Server started (PID: $EUREKA_PID)${NC}"
sleep 30  # Aguarda Eureka inicializar completamente

# 2. API Gateway
echo -e "${BLUE}Starting API Gateway...${NC}"
cd api-gateway
mvn spring-boot:run > ../logs/gateway.log 2>&1 &
GATEWAY_PID=$!
cd ..
echo -e "${GREEN}✓ API Gateway started (PID: $GATEWAY_PID)${NC}"
sleep 15

# 3. User Service
echo -e "${BLUE}Starting User Service...${NC}"
cd user-service
mvn spring-boot:run > ../logs/user.log 2>&1 &
USER_PID=$!
cd ..
echo -e "${GREEN}✓ User Service started (PID: $USER_PID)${NC}"
sleep 15

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
echo "📊 Service URLs:"
echo "  - Eureka Dashboard: http://localhost:8761"
echo "  - API Gateway:      http://localhost:8080"
echo "  - User Service:     http://localhost:8081"
echo "  - Reservation:      http://localhost:8082"
echo ""
echo "📖 API Documentation (Scalar):"
echo "  - User Service:     http://localhost:8081/scalar/index.html"
echo "  - Reservation:      http://localhost:8082/scalar/index.html"
echo ""
echo "🗄️  H2 Console:"
echo "  - User Service:     http://localhost:8081/h2-console"
echo "  - Reservation:      http://localhost:8082/h2-console"
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