package io.simakkoi9.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Value("${services.auth.name}")
    private String authServiceName;

    @Value("${services.passenger.name}")
    private String passengerServiceName;

    @Value("${services.driver.name}")
    private String driverServiceName;

    @Value("${services.rides.name}")
    private String ridesServiceName;

    @Value("${services.rating.name}")
    private String ratingServiceName;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .uri(authServiceName))

                .route("passenger-service", r -> r
                        .path("/api/v1/passengers/**")
                        .filters(f -> f.filter(new JwtUserIdFilter().apply(new JwtUserIdFilter.Config())))
                        .uri(passengerServiceName))

                .route("driver-service", r -> r
                        .path("/api/v1/drivers/**")
                        .filters(f -> f.filter(new JwtUserIdFilter().apply(new JwtUserIdFilter.Config())))
                        .uri(driverServiceName))

                .route("driver-service-cars", r -> r
                        .path("/api/v1/cars/**")
                        .filters(f -> f.filter(new JwtUserIdFilter().apply(new JwtUserIdFilter.Config())))
                        .uri(driverServiceName))

                .route("rides-service", r -> r
                        .path("/api/v1/rides/**")
                        .filters(f -> f.filter(new JwtUserIdFilter().apply(new JwtUserIdFilter.Config())))
                        .uri(ridesServiceName))

                .route("rating-service", r -> r
                        .path("/api/v1/ratings/**")
                        .filters(f -> f.filter(new JwtUserIdFilter().apply(new JwtUserIdFilter.Config())))
                        .uri(ratingServiceName))
                .build();
    }
} 