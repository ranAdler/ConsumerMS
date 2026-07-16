.PHONY: help up down build run test logs health clean restart ps db-shell kafka-console

help:
	@echo "Consumer Microservice - Available Commands"
	@echo ""
	@echo "Infrastructure:"
	@echo "  make up              Start Docker services (MySQL, Kafka, Zookeeper)"
	@echo "  make down            Stop and remove Docker services"
	@echo "  make ps              Show running containers"
	@echo "  make logs            View Docker logs"
	@echo ""
	@echo "Build & Run:"
	@echo "  make build           Build Maven project"
	@echo "  make run             Run application with Maven"
	@echo "  make run-jar         Run JAR file"
	@echo "  make docker-build    Build Docker image"
	@echo ""
	@echo "Testing & Monitoring:"
	@echo "  make test            Send test messages to Kafka"
	@echo "  make health          Check application health"
	@echo "  make logs-app        View application logs"
	@echo "  make db-shell        Connect to MySQL database"
	@echo "  make kafka-console   Open Kafka console"
	@echo "  make consumer-lag    Check Kafka consumer lag"
	@echo ""
	@echo "Development:"
	@echo "  make clean           Clean build artifacts"
	@echo "  make restart         Restart all services"
	@echo "  make reset           Remove all data and restart"
	@echo ""

# Infrastructure commands
up:
	@echo "Starting Docker services..."
	docker-compose up -d
	@echo "Waiting for services to be ready..."
	sleep 10
	@docker-compose ps

down:
	@echo "Stopping Docker services..."
	docker-compose down
	@echo "Services stopped"

ps:
	docker-compose ps

logs:
	docker-compose logs -f

restart:
	@echo "Restarting all services..."
	docker-compose restart
	@echo "Services restarted"

reset:
	@echo "Removing all containers and volumes..."
	docker-compose down -v
	@echo "Starting fresh setup..."
	docker-compose up -d
	@echo "Waiting for services to be ready..."
	sleep 15
	@docker-compose ps

# Build & Run commands
build:
	@echo "Building Maven project..."
	mvn clean package -q -DskipTests
	@echo "Build completed successfully"

run: build
	@echo "Starting application..."
	java -jar target/consumer-ms-1.0.0.jar

run-maven:
	mvn spring-boot:run

run-jar:
	java -jar target/consumer-ms-1.0.0.jar

docker-build:
	@echo "Building Docker image..."
	docker build -t consumer-ms:latest .
	@echo "Docker image built successfully"

# Testing & Monitoring commands
test:
	@echo "Running consumer tests..."
	chmod +x test-consumer.sh
	./test-consumer.sh

health:
	@echo "Checking application health..."
	@curl -s http://localhost:8080/api/actuator/health | jq .

logs-app:
	docker logs -f consumer-service

logs-db:
	docker logs -f consumer-mysql

logs-kafka:
	docker logs -f kafka

db-shell:
	docker exec -it consumer-mysql mysql -u consumer_user -pconsumer_password consumer_db

kafka-console:
	docker exec -it kafka bash

consumer-lag:
	docker exec kafka kafka-consumer-groups \
		--bootstrap-server kafka:9092 \
		--group consumer-service-group \
		--describe

# Topics management
list-topics:
	docker exec kafka kafka-topics --list --bootstrap-server=localhost:9092

describe-topics:
	@echo "message-create-topic:"
	docker exec kafka kafka-topics --describe --topic message-create-topic --bootstrap-server=localhost:9092
	@echo "\nmessage-update-topic:"
	docker exec kafka kafka-topics --describe --topic message-update-topic --bootstrap-server=localhost:9092
	@echo "\nmessage-delete-topic:"
	docker exec kafka kafka-topics --describe --topic message-delete-topic --bootstrap-server=localhost:9092
	@echo "\nmessage-read-topic:"
	docker exec kafka kafka-topics --describe --topic message-read-topic --bootstrap-server=localhost:9092

# Database commands
db-query:
	docker exec consumer-mysql mysql -u consumer_user -pconsumer_password consumer_db \
		-e "SELECT id, msg, operation, status, last_updated FROM messages ORDER BY last_updated DESC;"

db-clear:
	@echo "Clearing database tables..."
	docker exec consumer-mysql mysql -u consumer_user -pconsumer_password consumer_db \
		-e "TRUNCATE TABLE messages; TRUNCATE TABLE audit_log;"
	@echo "Database cleared"

# Development commands
clean:
	@echo "Cleaning build artifacts..."
	mvn clean
	rm -rf target/
	@echo "Clean completed"

info:
	@echo "Application Information:"
	@echo "Java Version: $$(java -version 2>&1 | head -1)"
	@echo "Maven Version: $$(mvn -v | head -1)"
	@echo "Docker Version: $$(docker --version)"
	@echo "Docker Compose Version: $$(docker-compose --version)"
	@echo ""
	@echo "Services Status:"
	@docker-compose ps 2>/dev/null || echo "Docker services not running"

format:
	@echo "No formatting config - using IDE defaults"

test-create:
	@docker exec -i kafka kafka-console-producer \
		--broker-list kafka:9092 \
		--topic message-create-topic \
		--property "parse.key=true" \
		--property "key.separator=:" \
		<<< '1:{"id": 100, "msg": "Test CREATE"}'
	@echo "CREATE message sent"

test-update:
	@docker exec -i kafka kafka-console-producer \
		--broker-list kafka:9092 \
		--topic message-update-topic \
		--property "parse.key=true" \
		--property "key.separator=:" \
		<<< '1:{"id": 100, "msg": "Test UPDATE"}'
	@echo "UPDATE message sent"

test-delete:
	@docker exec -i kafka kafka-console-producer \
		--broker-list kafka:9092 \
		--topic message-delete-topic \
		--property "parse.key=true" \
		--property "key.separator=:" \
		<<< '1:{"id": 100}'
	@echo "DELETE message sent"

test-read:
	@docker exec -i kafka kafka-console-producer \
		--broker-list kafka:9092 \
		--topic message-read-topic \
		--property "parse.key=true" \
		--property "key.separator=:" \
		<<< '1:{"id": 100}'
	@echo "READ message sent"