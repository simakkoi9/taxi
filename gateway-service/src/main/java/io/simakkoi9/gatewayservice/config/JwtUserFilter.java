package io.simakkoi9.gatewayservice.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUserFilter extends AbstractGatewayFilterFactory<JwtUserFilter.Config> {

    public JwtUserFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication().getPrincipal())
                .cast(Jwt.class)
                .map(jwt -> {
                    String userId = jwt.getSubject();
                    String userRole = jwt.getClaimAsStringList("roles")
                            .stream()
                            .filter(role -> role.startsWith("ROLE_"))
                            .findAny()
                            .orElse(null);
                    ServerHttpRequest mutatedExchange = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Role", userRole)
                            .build();
                    return exchange.mutate().request(mutatedExchange).build();
                })
                .flatMap(chain::filter);
    }

    public static class Config {

    }
} 