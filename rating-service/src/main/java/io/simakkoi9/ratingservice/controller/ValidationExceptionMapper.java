package io.simakkoi9.ratingservice.controller;

import io.simakkoi9.ratingservice.model.dto.ErrorResponse;
import io.simakkoi9.ratingservice.model.dto.MultiErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.Set;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();

        var errorMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(
                        new MultiErrorResponse(
                                errorMessages.stream()
                                        .map(message ->
                                                new ErrorResponse(
                                                        LocalDateTime.now(),
                                                        Response.Status.BAD_REQUEST.getStatusCode(),
                                                        message
                                                )
                                        )
                                        .toList()
                        )
                )
                .build();
    }
}
