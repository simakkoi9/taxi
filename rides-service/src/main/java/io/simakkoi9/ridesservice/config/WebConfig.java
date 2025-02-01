package io.simakkoi9.ridesservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebConfig {
    @Bean
    public WebClient openRouteWebClient() {
        return WebClient.create("https://api.openrouteservice.org");
    }
}
