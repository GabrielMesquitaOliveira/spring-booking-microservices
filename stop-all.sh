#!/bin/bash

echo "🛑 Stopping all microservices..."

# Criar pasta de PIDs se não existir
mkdir -p .pids

# Função para parar serviço
stop_service() {
    SERVICE_NAME=$1
    PID_FILE=".pids/${SERVICE_NAME}.pid"
    
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null 2>&1; then
            echo "Stopping $SERVICE_NAME (PID: $PID)..."
            kill $PID
            rm "$PID_FILE"
        else
            echo "$SERVICE_NAME not running"
            rm "$PID_FILE"
        fi
    else
        echo "No PID file for $SERVICE_NAME"
    fi
}

# Parar na ordem inversa
stop_service "reservation"
stop_service "user"
stop_service "gateway"
stop_service "eureka"

echo "✅ All services stopped"