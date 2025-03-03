package io.simakkoi9.passengerservice.config;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresTestContainer {
    private static final String IMAGE_VERSION = "postgres:16";
    private static final String DATABASE_NAME = "testdb";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpassword";

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse(IMAGE_VERSION))
                    .withDatabaseName(DATABASE_NAME)
                    .withUsername(USERNAME)
                    .withPassword(PASSWORD);

    static {
        POSTGRES_CONTAINER.start();
    }

    public static PostgreSQLContainer<?> getInstance() {
        return POSTGRES_CONTAINER;
    }
}
