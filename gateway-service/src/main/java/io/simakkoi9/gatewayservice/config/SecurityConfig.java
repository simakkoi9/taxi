package io.simakkoi9.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/v1/auth/**", "/actuator/**").permitAll()
                .pathMatchers("/api/v1/passengers/**").hasAnyRole("PASSENGER", "ADMIN")
                .pathMatchers("/api/v1/drivers/**").hasAnyRole("DRIVER", "ADMIN")
                .pathMatchers("/api/v1/cars/**").hasRole("ADMIN")
                .pathMatchers("/api/v1/rides/**").hasAnyRole("PASSENGER", "DRIVER", "ADMIN")
                .pathMatchers("/api/v1/ratings/**").hasAnyRole("PASSENGER", "DRIVER", "ADMIN")
                .anyExchange().authenticated()
            );

        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            Collection<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
            return Flux.fromIterable(authorities);
        });
        return jwtAuthenticationConverter;
    }
}
