package io.simakkoi9.ridesservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class TestWebClientConfig {
    @Bean
    public WebClient osrmWebClient() {
        return WebClient.create("http://localhost:8080");
    }
}
