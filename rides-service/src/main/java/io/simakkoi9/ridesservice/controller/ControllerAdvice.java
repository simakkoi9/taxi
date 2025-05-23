package io.simakkoi9.ridesservice.controller;

import feign.RetryableException;
import io.simakkoi9.ridesservice.exception.AvailableDriverProcessingException;
import io.simakkoi9.ridesservice.exception.BusyPassengerException;
import io.simakkoi9.ridesservice.exception.DistanceProcessingException;
import io.simakkoi9.ridesservice.exception.InvalidStatusException;
import io.simakkoi9.ridesservice.exception.NoAvailableDriversException;
import io.simakkoi9.ridesservice.exception.PassengerAccessDeniedException;
import io.simakkoi9.ridesservice.exception.PassengerNotFoundException;
import io.simakkoi9.ridesservice.exception.PassengerServiceNotAvailableException;
import io.simakkoi9.ridesservice.exception.RideNotFoundException;
import io.simakkoi9.ridesservice.model.dto.rest.response.ErrorResponse;
import io.simakkoi9.ridesservice.util.MessageKeyConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ErrorResponse> handleCircuitBreakerException(CallNotPermittedException e) {
        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                new PassengerServiceNotAvailableException(
                        MessageKeyConstants.PASSENGER_TEMP_NOT_AVAILABLE_ERROR,
                        messageSource
                )
        );
    }

    @ExceptionHandler({
            RetryableException.class
    })
    public ResponseEntity<ErrorResponse> handleRetryableException(RuntimeException e) {
        return buildErrorResponse(HttpStatus.GATEWAY_TIMEOUT, e);
    }

    @ExceptionHandler({
        PassengerServiceNotAvailableException.class,
        AvailableDriverProcessingException.class
    })
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(RuntimeException e) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, e);
    }

    @ExceptionHandler({
        NoAvailableDriversException.class,
        RideNotFoundException.class,
        PassengerNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(BusyPassengerException.class)
    public ResponseEntity<ErrorResponse> handleBusyPassengerExceptions(BusyPassengerException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler({
        DistanceProcessingException.class
    })
    public ResponseEntity<ErrorResponse> handleDistanceProcessingException(RuntimeException e) {
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, e);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusException(RuntimeException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationExceptions(ConstraintViolationException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(PassengerAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handlePassengerAccessDeniedException(RuntimeException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, Exception e) {
        List<String> errors = new ArrayList<>();

        if (e instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) e)
                    .getBindingResult()
                    .getAllErrors()
                    .forEach(
                        error -> {
                            error.getDefaultMessage();
                            errors.add(error.getDefaultMessage());
                        }
                );
        } else if (e instanceof ConstraintViolationException) {
            ((ConstraintViolationException) e)
                    .getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(errors::add);

        } else if (e instanceof RetryableException) {
            errors.add(messageSource.getMessage(
                    MessageKeyConstants.PASSENGER_NOT_AVAILABLE_ERROR,
                    new Object[]{},
                    LocaleContextHolder.getLocale())
            );
        } else {
            errors.add(e.getMessage() != null ? e.getMessage() :
                    messageSource.getMessage(
                            MessageKeyConstants.INTERNAL_SERVER_ERROR,
                            new Object[]{},
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                errors
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}
