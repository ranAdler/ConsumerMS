#!/bin/bash

# Test Consumer Microservice Script
# This script sends test messages to Kafka topics to verify the consumer is working

set -e

KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
KAFKA_CONTAINER="kafka"

echo "================================================"
echo "Consumer Microservice Test Script"
echo "================================================"
echo ""

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to send message to Kafka
send_message() {
    local topic=$1
    local key=$2
    local message=$3

    echo -e "${BLUE}Sending to topic: $topic${NC}"
    echo -e "${YELLOW}Key: $key${NC}"
    echo -e "${YELLOW}Message: $message${NC}"

    docker exec -i $KAFKA_CONTAINER kafka-console-producer \
        --broker-list $KAFKA_BOOTSTRAP_SERVERS \
        --topic $topic \
        --property "parse.key=true" \
        --property "key.separator=:" \
        <<< "$key:$message"

    echo -e "${GREEN}✓ Message sent successfully${NC}"
    echo ""
}

# Function to check database
check_database() {
    echo -e "${BLUE}Querying database...${NC}"
    docker exec consumer-mysql mysql -u consumer_user -pconsumer_password consumer_db \
        -e "SELECT id, msg, operation, status, last_updated FROM messages ORDER BY last_updated DESC LIMIT 5;"
    echo ""
}

# Main test flow
echo -e "${BLUE}Starting Consumer Tests...${NC}"
echo ""

# Test 1: CREATE operation
echo -e "${BLUE}=== Test 1: CREATE Operation ===${NC}"
send_message "message-create-topic" "1" '{"id": 100, "msg": "Test message created"}'
sleep 2

# Test 2: UPDATE operation
echo -e "${BLUE}=== Test 2: UPDATE Operation ===${NC}"
send_message "message-update-topic" "1" '{"id": 100, "msg": "Test message updated"}'
sleep 2

# Test 3: READ operation
echo -e "${BLUE}=== Test 3: READ Operation ===${NC}"
send_message "message-read-topic" "1" '{"id": 100}'
sleep 1

# Test 4: CREATE another message
echo -e "${BLUE}=== Test 4: CREATE another message ===${NC}"
send_message "message-create-topic" "2" '{"id": 101, "msg": "Another test message"}'
sleep 2

# Test 5: DELETE operation
echo -e "${BLUE}=== Test 5: DELETE Operation ===${NC}"
send_message "message-delete-topic" "1" '{"id": 100}'
sleep 2

# Check database results
echo -e "${BLUE}=== Database Results ===${NC}"
check_database

echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}All tests completed!${NC}"
echo -e "${GREEN}================================================${NC}"
echo ""
echo -e "${BLUE}Application Logs:${NC}"
docker logs consumer-service 2>&1 | tail -20