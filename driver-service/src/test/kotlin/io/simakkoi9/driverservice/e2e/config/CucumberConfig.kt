package io.simakkoi9.driverservice.e2e.config

import io.cucumber.spring.CucumberContextConfiguration
import io.simakkoi9.driverservice.config.PostgresTestContainer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.context.ImportTestcontainers
import org.springframework.test.context.ActiveProfiles


@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ImportTestcontainers(PostgresTestContainer::class)
class CucumberConfig {

}