package io.simakkoi9.ratingservice.controller;

import io.simakkoi9.ratingservice.exception.DuplicateRatingException;
import io.simakkoi9.ratingservice.exception.RatingNotFoundException;
import io.simakkoi9.ratingservice.model.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;

@Provider
public class RatingExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable e) {

        if (e instanceof DuplicateRatingException){
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(
                            new ErrorResponse(
                                    LocalDateTime.now(),
                                    Response.Status.CONFLICT.getStatusCode(),
                                    e.getMessage()
                            )
                    )
                    .build();
        } else if (e instanceof RatingNotFoundException) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(
                            new ErrorResponse(
                                    LocalDateTime.now(),
                                    Response.Status.NOT_FOUND.getStatusCode(),
                                    e.getMessage()
                            )
                    )
                    .build();
        } else if (e instanceof ConstraintViolationException) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(
                            new ErrorResponse(
                                    LocalDateTime.now(),
                                    Response.Status.BAD_REQUEST.getStatusCode(),
                                    e.getMessage()
                            )
                    )
                    .build();
        }
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(
                        new ErrorResponse(
                                LocalDateTime.now(),
                                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                e.getMessage()
                        )
                )
                .build();
    }
}
