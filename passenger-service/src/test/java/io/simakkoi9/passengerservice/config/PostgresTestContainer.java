package io.simakkoi9.passengerservice.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class PostgresTestContainer {
    private static final String IMAGE_VERSION = "postgres:16";
    private static final String DATABASE_NAME = "testdb";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpassword";

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse(IMAGE_VERSION))
                    .withDatabaseName(DATABASE_NAME)
                    .withUsername(USERNAME)
                    .withPassword(PASSWORD);

    static {
        postgreSQLContainer.start();
    }
}
