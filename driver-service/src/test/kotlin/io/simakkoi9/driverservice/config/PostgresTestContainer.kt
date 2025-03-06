package io.simakkoi9.driverservice.config

import org.testcontainers.containers.PostgreSQLContainer

class PostgresTestContainer {
    companion object {
        private const val IMAGE_VERSION = "postgres:16"
        private const val DATABASE_NAME = "testdb"
        private const val USERNAME = "test"
        private const val PASSWORD = "test"

        private val POSTGRES_CONTAINER = PostgreSQLContainer(IMAGE_VERSION)
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)

        init {
            POSTGRES_CONTAINER.start()
        }

        fun getInstance(): PostgreSQLContainer<*> {
            return POSTGRES_CONTAINER
        }
    }
} 