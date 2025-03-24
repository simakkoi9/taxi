package io.simakkoi9.ridesservice.e2e.config;

import io.simakkoi9.ridesservice.config.TestContainersConfig;
import io.simakkoi9.ridesservice.config.TestKafkaConfig;
import io.simakkoi9.ridesservice.config.TestWebClientConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 9090)
@Import({
    TestKafkaConfig.class,
    TestContainersConfig.class,
    TestWebClientConfig.class
})
public class CucumberSpringConfiguration {
    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.data.mongodb.uri",
                () -> TestContainersConfig.mongoContainer.getReplicaSetUrl("test_db")
        );
        registry.add(
                "spring.kafka.bootstrap-servers",
                () -> TestContainersConfig.kafkaContainer.getBootstrapServers()
        );
    }
} 