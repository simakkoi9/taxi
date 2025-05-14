package io.simakkoi9.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceConfig {

    @Value("${service.passenger.name}")
    private String passengerServiceName;

    @Value("${service.driver.name}")
    private String driverServiceName;

    @Bean
    public WebClient passengerServiceClient(WebClient.Builder builder) {
        return builder.baseUrl("http://" + passengerServiceName).build();
    }

    @Bean
    public WebClient driverServiceClient(WebClient.Builder builder) {
        return builder.baseUrl("http://" + driverServiceName).build();
    }
} 