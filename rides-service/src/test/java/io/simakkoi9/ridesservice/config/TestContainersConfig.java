package io.simakkoi9.ridesservice.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ActiveProfiles("test")
public class TestContainersConfig {
    private static final DockerImageName KAFKA_IMAGE =
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0");
    private static final DockerImageName MONGO_IMAGE =
            DockerImageName.parse("mongo:6");

    @Container
    @ServiceConnection
    public static ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(KAFKA_IMAGE)
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoContainer = new MongoDBContainer(MONGO_IMAGE)
            .withEnv("MONGO_INITDB_DATABASE", "test_db")
            .withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS));

    static {
        mongoContainer.start();
        kafkaContainer.start();
    }
}
