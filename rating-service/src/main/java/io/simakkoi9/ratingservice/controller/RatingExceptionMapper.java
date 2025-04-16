package io.simakkoi9.ratingservice.controller;

import io.simakkoi9.ratingservice.config.message.MessageConfig;
import io.simakkoi9.ratingservice.exception.DriverAlreadyRatedException;
import io.simakkoi9.ratingservice.exception.DuplicateRatingException;
import io.simakkoi9.ratingservice.exception.InvalidValidationMessageKeyException;
import io.simakkoi9.ratingservice.exception.NoRatesException;
import io.simakkoi9.ratingservice.exception.PassengerAlreadyRatedException;
import io.simakkoi9.ratingservice.exception.RatingNotFoundException;
import io.simakkoi9.ratingservice.exception.RideJsonProcessingException;
import io.simakkoi9.ratingservice.exception.RideNotFoundException;
import io.simakkoi9.ratingservice.exception.UncompletedRideException;
import io.simakkoi9.ratingservice.model.dto.rest.ErrorResponse;
import io.simakkoi9.ratingservice.util.MessageKeyConstants;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.LocalDateTime;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

@Provider
public class RatingExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Inject
    MessageConfig messageConfig;

    @Override
    public Response toResponse(RuntimeException e) {
        if (
                e instanceof DuplicateRatingException
                || e instanceof DriverAlreadyRatedException
                || e instanceof PassengerAlreadyRatedException
        ) {
            return buildResponse(Response.Status.CONFLICT, e);
        } else if (
                e instanceof RatingNotFoundException
                || e instanceof NoRatesException
                || e instanceof RideNotFoundException
        ) {
            return buildResponse(Response.Status.NOT_FOUND, e);
        } else if (
                e instanceof InvalidValidationMessageKeyException
                || e instanceof RideJsonProcessingException
                || e instanceof UncompletedRideException
        ) {
            return buildResponse(Response.Status.BAD_REQUEST, e);
        } else if (
                e instanceof ProcessingException
                || e instanceof TimeoutException
        ) {
            return buildResponse(
                    Response.Status.GATEWAY_TIMEOUT,
                    MessageKeyConstants.EXTERNAL_SERVICE_TIMEOUT_ERROR
            );
        } else if (
                e instanceof CircuitBreakerOpenException
        ) {
            return buildResponse(
                    Response.Status.SERVICE_UNAVAILABLE,
                    MessageKeyConstants.EXTERNAL_SERVICE_UNAVAILABLE_ERROR
            );
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            new ErrorResponse(
                                    LocalDateTime.now(),
                                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                    messageConfig.getMessage(MessageKeyConstants.INTERNAL_SERVER_ERROR)
                            )
                    )
                    .build();
        }
    }

    private Response buildResponse(Response.Status status, RuntimeException e) {
        return Response.status(status)
                .entity(new ErrorResponse(
                        LocalDateTime.now(),
                        status.getStatusCode(),
                        e.getMessage()
                ))
                .build();
    }

    private Response buildResponse(Response.Status status, String messageKey) {
        return Response.status(status)
                .entity(new ErrorResponse(
                        LocalDateTime.now(),
                        status.getStatusCode(),
                        messageConfig.getMessage(messageKey)
                ))
                .build();
    }

}
