# Taxi Microservices
Project for taxi aggregator that contains 7 modules:
# passenger-service
Service with passenger CRUD.
Instruments:
    - Java Spring
    - PostgreSQL
    - Liquibase
    - Lombok
    - Mapstruct
# driver service
Service with driver CRUD.
Instruments:
    - Kotlin Spring
    - PostgreSQL
    - Liquibase
    - Mapstruct
    - Kafka
# rides service
Service for rides creating and management.
Instruments:
    - Java Spring
    - MongoDB
    - Kafka
    - OpenFeign
    - Lombok
    - Mapstruct
# rating service
Service for rides' rating creating and management.
Instruments:
    - Java Quarkus
    - PostgreSQL
    - Flyway
    - Kafka
    - Rest Client
    - Lombok
    - Mapstruct
# eureka and gateway services
Discovery, registry and API gateway services with Eureka.
# auth-service
Service for user registration, authentication and authorization.
Instruments:
    - Java Spring
    - Spring Security
    - Keycloak
    - Lombok

# Service testing
Unit, integration and E2E test for services.
Instruments:
    - JUnit 5
    - Rest Assured
    - Cucumber
    - Testcontainers

# Service monitoring
Stack for metrics collecting, log stashing and request tracing.
Instruments:
    - Prometheus
    - Loki
    - Promtail
    - Log4j
    - Tempo
    - Zipkin
    - OTLP(in rating service)
    - Grafana