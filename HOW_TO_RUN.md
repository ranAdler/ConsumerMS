# How to Run Consumer Microservice

Complete guide for building, running, and testing the Consumer Microservice in different environments.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Quick Start (5 minutes)](#quick-start-5-minutes)
3. [Detailed Setup](#detailed-setup)
4. [Running the Application](#running-the-application)
5. [Testing](#testing)
6. [Docker Deployment](#docker-deployment)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required
- Java 21 JDK
- Maven 3.9.0+
- Docker & Docker Compose
- Git

### Optional
- IDE (IntelliJ IDEA, VS Code)
- Kafka CLI tools
- MySQL CLI

### System Requirements
- 4GB RAM minimum
- 10GB disk space
- Linux, macOS, or Windows (with WSL2)

## Quick Start (5 minutes)

The fastest way to get the application running:

```bash
# 1. Start all infrastructure (MySQL, Kafka, Zookeeper)
docker-compose up -d

# 2. Build the application
mvn clean package -DskipTests

# 3. Run the application
java -jar target/consumer-ms-1.0.0.jar

# 4. Verify it's running
curl http://localhost:8080/api/actuator/health

# 5. Send test messages
./test-consumer.sh
```

The application is now running on `http://localhost:8080/api`

## Detailed Setup

### Step 1: Verify Prerequisites

```bash
# Check Java version
java -version
# Should show: openjdk 21.x.x or similar

# Check Maven version
mvn -version
# Should show: Maven 3.9.0+

# Check Docker
docker --version
docker-compose --version
```

### Step 2: Clone or Prepare Repository

```bash
# If cloning
git clone https://github.com/yourusername/ConsumerMS.git
cd ConsumerMS

# If already in repository
git pull origin main
```

### Step 3: Start Infrastructure Services

```bash
# Start Docker services (MySQL, Kafka, Zookeeper)
docker-compose up -d

# Verify services are running
docker-compose ps

# Expected output should show:
# - consumer-mysql (UP)
# - zookeeper (UP)
# - kafka (UP)
# - kafka-init (completed/exited)
```

### Step 4: Build Application

```bash
# Clean build
mvn clean package -DskipTests

# Should output: BUILD SUCCESS
# Creates: target/consumer-ms-1.0.0.jar
```

### Step 5: Run Application

Choose one method:

#### Method A: Run JAR directly
```bash
java -jar target/consumer-ms-1.0.0.jar
```

#### Method B: Run with Maven
```bash
mvn spring-boot:run
```

#### Method C: Run in Docker container
```bash
# Build image
docker build -t consumer-ms:latest .

# Run container
docker run -d \
  --name consumer-service \
  -p 8081:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/consumer_db \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  consumer-ms:latest

# Verify running
docker logs consumer-service
```

### Step 6: Verify Application Started

```bash
# Check health endpoint
curl http://localhost:8080/api/actuator/health

# Expected response
# {"status":"UP"}

# View logs
tail -f /tmp/app.log
# Or if running in Docker
docker logs -f consumer-service
```

## Running the Application

### Using Docker Compose (Recommended)

Start all services together:

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Remove all data and restart
docker-compose down -v
docker-compose up -d
```

### Using Maven for Development

```bash
# Start infrastructure
docker-compose up -d

# Terminal 1: Build and run
mvn spring-boot:run

# Terminal 2: Send messages or run tests
./test-consumer.sh

# Terminal 3: Monitor database
docker exec consumer-mysql mysql -u consumer_user -p consumer_db
# Password: consumer_password
```

### Using JAR for Production

```bash
# Start infrastructure
docker-compose up -d

# Run JAR
java -jar target/consumer-ms-1.0.0.jar

# Or with custom port
java -jar -Dserver.port=9000 target/consumer-ms-1.0.0.jar

# Or with environment variables
export SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
export SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/consumer_db
java -jar target/consumer-ms-1.0.0.jar
```

## Testing

### Automated Test Suite

Run the complete test suite:

```bash
chmod +x test-consumer.sh
./test-consumer.sh
```

This sends actual Kafka messages and verifies:
- ✓ CREATE operations
- ✓ UPDATE operations
- ✓ DELETE operations
- ✓ READ operations
- ✓ Database persistence

### Manual Testing

Send individual messages:

```bash
# 1. CREATE a message
docker exec -i kafka kafka-console-producer \
  --broker-list kafka:9092 \
  --topic message-create-topic \
  --property "parse.key=true" \
  --property "key.separator=:" \
  <<< '1:{"id": 100, "msg": "Test message"}'

# 2. UPDATE a message
docker exec -i kafka kafka-console-producer \
  --broker-list kafka:9092 \
  --topic message-update-topic \
  --property "parse.key=true" \
  --property "key.separator=:" \
  <<< '1:{"id": 100, "msg": "Updated message"}'

# 3. READ a message
docker exec -i kafka kafka-console-producer \
  --broker-list kafka:9092 \
  --topic message-read-topic \
  --property "parse.key=true" \
  --property "key.separator=:" \
  <<< '1:{"id": 100}'

# 4. DELETE a message
docker exec -i kafka kafka-console-producer \
  --broker-list kafka:9092 \
  --topic message-delete-topic \
  --property "parse.key=true" \
  --property "key.separator=:" \
  <<< '1:{"id": 100}'
```

### Verify in Database

```bash
# Connect to database
docker exec -it consumer-mysql mysql -u consumer_user -p consumer_db
# Password: consumer_password

# Query messages
SELECT * FROM messages;

# Check by ID
SELECT * FROM messages WHERE id = 100;

# Exit
EXIT;
```

### Check Application Logs

```bash
# View latest logs
tail -20 /tmp/app.log

# Search for operations
grep "CREATE\|UPDATE\|DELETE\|READ" /tmp/app.log

# Or in Docker
docker logs consumer-service | grep "✓"
```

## Docker Deployment

### Build Docker Image

```bash
# Build image
docker build -t consumer-ms:latest .

# Verify image
docker images | grep consumer-ms

# Expected: consumer-ms:latest (412MB)
```

### Run in Docker

#### Standalone Docker
```bash
docker run -d \
  --name consumer-service \
  -p 8081:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/consumer_db \
  -e SPRING_DATASOURCE_USERNAME=consumer_user \
  -e SPRING_DATASOURCE_PASSWORD=consumer_password \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  consumer-ms:latest

# Check logs
docker logs -f consumer-service

# Check health
curl http://localhost:8081/api/actuator/health

# Stop container
docker stop consumer-service
docker rm consumer-service
```

#### With Docker Compose
```bash
# Uses docker-compose.yml with full stack
docker-compose up -d

# View all services
docker-compose ps

# Stop all
docker-compose down
```

### Push to Docker Registry

```bash
# Tag image
docker tag consumer-ms:latest myregistry/consumer-ms:1.0.0

# Login to registry
docker login myregistry

# Push
docker push myregistry/consumer-ms:1.0.0

# Pull and run
docker pull myregistry/consumer-ms:1.0.0
docker run -d \
  -p 8080:8080 \
  myregistry/consumer-ms:1.0.0
```

## Kubernetes Deployment

### Prerequisites
- kubectl configured
- Kubernetes cluster running
- Docker image pushed to registry

### Create Kubernetes Manifests

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: consumer-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: consumer-service
  template:
    metadata:
      labels:
        app: consumer-service
    spec:
      containers:
      - name: consumer-ms
        image: myregistry/consumer-ms:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: consumer-config
              key: db-url
        - name: SPRING_KAFKA_BOOTSTRAP_SERVERS
          valueFrom:
            configMapKeyRef:
              name: consumer-config
              key: kafka-servers
        livenessProbe:
          httpGet:
            path: /api/actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/actuator/health
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
```

### Deploy to Kubernetes

```bash
# Create namespace
kubectl create namespace consumer-ms

# Create ConfigMap for configuration
kubectl create configmap consumer-config \
  --from-literal=db-url=jdbc:mysql://mysql-service:3306/consumer_db \
  --from-literal=kafka-servers=kafka-service:9092 \
  -n consumer-ms

# Deploy application
kubectl apply -f deployment.yaml -n consumer-ms

# Verify deployment
kubectl get pods -n consumer-ms
kubectl logs -f deployment/consumer-service -n consumer-ms

# Check service
kubectl get svc -n consumer-ms

# Port forward for testing
kubectl port-forward svc/consumer-service 8080:8080 -n consumer-ms
```

## Troubleshooting

### Container Won't Start

**Error: Connection refused**
```bash
# Check if MySQL is running
docker ps | grep mysql

# If not, start it
docker-compose up -d consumer-db

# Wait 10 seconds for MySQL to initialize
sleep 10

# Try again
docker-compose up -d
```

**Error: Port already in use**
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Or use different port
java -Dserver.port=9000 -jar target/consumer-ms-1.0.0.jar
```

### Connection Issues

**Kafka connection failed**
```bash
# Verify Kafka is running
docker exec kafka kafka-broker-api-versions --bootstrap-server=localhost:9092

# Check bootstrap servers config
docker ps | grep kafka
curl -s localhost:9092

# Reconnect
docker-compose down
docker-compose up -d
```

**MySQL connection refused**
```bash
# Start MySQL
docker start consumer-mysql

# Verify connection
docker exec consumer-mysql mysql -u consumer_user -p -e "SELECT 1"

# Or restart all services
docker-compose down -v
docker-compose up -d
```

### Application Errors

**No messages being consumed**
```bash
# Check consumer group
docker exec kafka kafka-consumer-groups \
  --bootstrap-server kafka:9092 \
  --group consumer-service-group \
  --describe

# Check topic has messages
docker exec kafka kafka-console-consumer \
  --bootstrap-server kafka:9092 \
  --topic message-create-topic \
  --from-beginning \
  --max-messages 10

# Check application logs
tail -50 /tmp/app.log | grep ERROR
```

**Database not persisting**
```bash
# Check table exists
docker exec consumer-mysql mysql \
  -u consumer_user -p consumer_db \
  -e "DESCRIBE messages;"

# Check data
docker exec consumer-mysql mysql \
  -u consumer_user -p consumer_db \
  -e "SELECT COUNT(*) FROM messages;"

# Check application logs for errors
grep "ERROR\|Exception" /tmp/app.log
```

### Performance Issues

**High consumer lag**
```bash
# Check lag
docker exec kafka kafka-consumer-groups \
  --bootstrap-server kafka:9092 \
  --group consumer-service-group \
  --describe

# Increase max.poll.records in application.yml
# Restart application

# Monitor CPU and memory
docker stats consumer-service
```

**Slow database queries**
```bash
# Enable slow query log
docker exec consumer-mysql mysql \
  -u root -p \
  -e "SET GLOBAL slow_query_log = 'ON';"

# Check query performance
docker exec consumer-mysql mysql \
  -u consumer_user -p consumer_db \
  -e "EXPLAIN SELECT * FROM messages WHERE id = 100;"
```

## Useful Commands

### Makefile (Recommended)

```bash
# Show all commands
make help

# Start infrastructure
make up

# Build application
make build

# Run application
make run-jar

# Run tests
make test

# View logs
make logs-app

# Database operations
make db-shell
make db-query
make db-clear

# Stop everything
make down
```

### Manual Docker Commands

```bash
# View container logs
docker logs consumer-service
docker logs -f consumer-service

# Connect to container
docker exec -it consumer-service bash

# View running processes
docker ps

# View all containers
docker ps -a

# View logs from multiple services
docker-compose logs -f

# View resource usage
docker stats
```

### Manual Kafka Commands

```bash
# List topics
docker exec kafka kafka-topics --list --bootstrap-server=kafka:9092

# Describe topic
docker exec kafka kafka-topics --describe --topic message-create-topic --bootstrap-server=kafka:9092

# Consumer group info
docker exec kafka kafka-consumer-groups \
  --bootstrap-server kafka:9092 \
  --group consumer-service-group \
  --describe

# Reset consumer offset
docker exec kafka kafka-consumer-groups \
  --bootstrap-server kafka:9092 \
  --group consumer-service-group \
  --reset-offsets \
  --to-earliest \
  --execute
```

## Next Steps

1. ✅ Start infrastructure
2. ✅ Build application
3. ✅ Run application
4. ✅ Send test messages
5. ✅ Verify database
6. 📝 Customize configuration
7. 📝 Deploy to your environment

## Support

For issues or questions:
- Check [Troubleshooting](#troubleshooting) section
- Review application logs
- Check [README.md](./README.md) for architecture overview
- Review [TEST_REPORT.md](./TEST_REPORT.md) for test results