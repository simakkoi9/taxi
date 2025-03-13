package io.simakkoi9.ridesservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("!test")
public class WebClientConfig {
    @Bean
    public WebClient osrmWebClient() {
        return WebClient.create("http://router.project-osrm.org");
    }
}
