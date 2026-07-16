#!/bin/bash

# Build and Run Consumer Microservice
# This script sets up the infrastructure and runs the application

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}Consumer Microservice - Build and Run${NC}"
echo -e "${BLUE}================================================${NC}"
echo ""

# Step 1: Start Docker services
echo -e "${YELLOW}Step 1: Starting Docker services...${NC}"
docker-compose up -d
echo -e "${GREEN}✓ Docker services started${NC}"
echo ""

# Wait for services to be ready
echo -e "${YELLOW}Waiting for services to be ready...${NC}"
sleep 15

# Step 2: Build Maven project
echo -e "${YELLOW}Step 2: Building Maven project...${NC}"
mvn clean package -q -DskipTests
echo -e "${GREEN}✓ Maven build completed${NC}"
echo ""

# Step 3: Run the application
echo -e "${YELLOW}Step 3: Starting Consumer Application...${NC}"
java -jar target/consumer-ms-1.0.0.jar &
APP_PID=$!
echo -e "${GREEN}✓ Application started (PID: $APP_PID)${NC}"
echo ""

# Wait for app to start
sleep 5

# Step 4: Verify application is running
echo -e "${YELLOW}Step 4: Verifying application...${NC}"
if curl -s http://localhost:8080/api/actuator/health | grep -q "UP"; then
    echo -e "${GREEN}✓ Application is running and healthy${NC}"
else
    echo -e "${YELLOW}⚠ Application might still be starting...${NC}"
fi
echo ""

echo -e "${BLUE}================================================${NC}"
echo -e "${GREEN}Setup Complete!${NC}"
echo -e "${BLUE}================================================${NC}"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo -e "1. Run tests: ${YELLOW}./test-consumer.sh${NC}"
echo -e "2. View logs: ${YELLOW}docker logs -f consumer-service${NC}"
echo -e "3. Query DB: ${YELLOW}docker exec consumer-mysql mysql -u consumer_user -pconsumer_password consumer_db${NC}"
echo -e "4. Health check: ${YELLOW}curl http://localhost:8080/api/actuator/health${NC}"
echo ""
echo -e "${BLUE}To stop services: ${YELLOW}docker-compose down${NC}"
echo ""