package io.simakkoi9.ratingservice.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.concurrent.TimeUnit;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.spi.RestClientBuilderListener;

@ApplicationScoped
public class RestClientConfig implements RestClientBuilderListener {

    @Override
    public void onNewBuilder(RestClientBuilder builder) {
        builder
            .register(new ClientRequestFilter() {
                @Context
                HttpHeaders headers;

                @Override
                public void filter(ClientRequestContext requestContext) {
                    String userId = headers.getHeaderString("X-User-Id");
                    String userRole = headers.getHeaderString("X-User-Role");
                    if (userId != null) {
                        requestContext.getHeaders().add("X-User-Id", userId);
                    }
                    if (userRole != null) {
                        requestContext.getHeaders().add("X-User-Role", userRole);
                    }
                }
            })
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS);
    }
} 