package io.simakkoi9.driverservice.config

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
object PostgresTestContainer {

    private const val IMAGE_VERSION = "postgres:16"
    private const val DATABASE_NAME = "testdb"
    private const val USERNAME = "test"
    private const val PASSWORD = "test"

    @Container
    @ServiceConnection
    val POSTGRES_CONTAINER = PostgreSQLContainer(IMAGE_VERSION)
        .withDatabaseName(DATABASE_NAME)
        .withUsername(USERNAME)
        .withPassword(PASSWORD)

} 