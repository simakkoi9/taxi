package io.simakkoi9.ratingservice.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import org.jboss.logging.Logger;

@Provider
public class LoggingConfig implements ContainerRequestFilter {
    private static final Logger LOG = Logger.getLogger(LoggingConfig.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getRequestUri().getPath();

        LOG.infof("HTTP REQUEST: %s %s\nHeaders: %s\nBody: %s", method, path);
    }

}
