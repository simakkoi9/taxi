package io.simakkoi9.ridesservice.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class TestContainersConfig {
    private static final DockerImageName IMAGE = DockerImageName.parse("confluentinc/cp-kafka:7.4.0");

    @Container
    @ServiceConnection
    public static ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(IMAGE)
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");

    static {
        kafkaContainer.start();
    }
}
