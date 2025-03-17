package io.simakkoi9.ratingservice.config;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.HashMap;
import java.util.Map;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainersConfig implements QuarkusTestResourceLifecycleManager {
    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:16");
    private static final DockerImageName KAFKA_IMAGE =
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0");
    private static final String DATABASE_NAME = "rating_test_db";
    private static final String DATABASE_USER = "test";
    private static final String DATABASE_PASSWORD = "test";

    public static final ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(KAFKA_IMAGE)
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName(DATABASE_NAME)
            .withUsername(DATABASE_USER)
            .withPassword(DATABASE_PASSWORD);

    @Override
    public Map<String, String> start() {
        postgresContainer.start();
        kafkaContainer.start();
        Map<String, String> config = new HashMap<>();

        config.put("quarkus.datasource.jdbc.url", postgresContainer.getJdbcUrl());
        config.put("quarkus.datasource.username", postgresContainer.getUsername());
        config.put("quarkus.datasource.password", postgresContainer.getPassword());
        config.put("kafka.bootstrap.servers", kafkaContainer.getBootstrapServers());
        
        return config;
    }

    @Override
    public void stop() {
        if (postgresContainer != null) {
            postgresContainer.stop();
        }
        if (kafkaContainer != null) {
            kafkaContainer.stop();
        }
    }
}
