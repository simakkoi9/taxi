package io.simakkoi9.gatewayservice.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtUserIdFilter extends AbstractGatewayFilterFactory<JwtUserIdFilter.Config> {

    public JwtUserIdFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return ReactiveSecurityContextHolder.getContext()
                    .map(context -> context.getAuthentication().getPrincipal())
                    .cast(Jwt.class)
                    .map(jwt -> {
                        String userId = jwt.getSubject();
                        exchange.getRequest().mutate()
                                .header("X-User-Id", userId)
                                .build();
                        return exchange;
                    })
                    .flatMap(chain::filter)
                    .switchIfEmpty(chain.filter(exchange));
        };
    }

    public static class Config {

    }
} 