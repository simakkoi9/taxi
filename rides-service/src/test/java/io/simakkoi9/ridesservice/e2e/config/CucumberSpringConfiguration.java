package io.simakkoi9.ridesservice.e2e.config;

import io.simakkoi9.ridesservice.config.TestContainersConfig;
import io.simakkoi9.ridesservice.config.TestKafkaConfig;
import io.simakkoi9.ridesservice.config.TestWebClientConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8080)
@Import({
    TestKafkaConfig.class,
    TestContainersConfig.class,
    TestWebClientConfig.class
})
public class CucumberSpringConfiguration {
} 