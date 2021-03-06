# Quotes

A little microservice providing quoted price of stock of public companies via HATEOAS HAL API

## Set up

### Requirements
* Java 17
* Docker Engine 20.10.12
* Docker Compose 1.26.0
* ports 3306, 8080, 8081 to be free

### 1. Build

./mvnw -f build-tools/pom.xml clean install

./mvnw clean install

### 2. Run

#### Usage

* root resource will be available at http://localhost:8081
* API is enablead for self-serve, includes live in-service documentation

#### Deploy (dev-env)

docker-compose -f quotes-service/docker-compose.yml up

#### Destroy (dev-env)

docker-compose -f quotes-service/docker-compose.yml down

### 3. Performance Tests (against dev-env)

./mvnw -f quotes-performance/pom.xml gatling:test
