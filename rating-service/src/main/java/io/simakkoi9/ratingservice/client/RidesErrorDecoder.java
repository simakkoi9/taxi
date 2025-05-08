package io.simakkoi9.ratingservice.client;

import io.simakkoi9.ratingservice.config.message.MessageConfig;
import io.simakkoi9.ratingservice.exception.RideNotFoundException;
import io.simakkoi9.ratingservice.util.MessageKeyConstants;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@ApplicationScoped
public class RidesErrorDecoder implements ResponseExceptionMapper<RuntimeException> {

    @Inject
    MessageConfig messageConfig;

    @Override
    public RuntimeException toThrowable(Response response) {
        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            return new RideNotFoundException(MessageKeyConstants.RIDE_NOT_FOUND, messageConfig);
        }
        return new WebApplicationException(response);
    }
}