package io.simakkoi9.ridesservice.controller;

import io.simakkoi9.ridesservice.exception.BusyPassengerException;
import io.simakkoi9.ridesservice.exception.DistanceProcessingException;
import io.simakkoi9.ridesservice.exception.NoAvailableDriversException;
import io.simakkoi9.ridesservice.exception.RideNotFoundException;
import io.simakkoi9.ridesservice.model.dto.rest.response.ErrorResponse;
import io.simakkoi9.ridesservice.util.MessageKeyConstants;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @ExceptionHandler({
        NoAvailableDriversException.class,
        RideNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(BusyPassengerException.class)
    public ResponseEntity<ErrorResponse> handleBusyPassengerExceptions(BusyPassengerException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(DistanceProcessingException.class)
    public ResponseEntity<ErrorResponse> handleDistanceProcessingException(DistanceProcessingException e) {
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(HttpMessageNotReadableException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e);
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
