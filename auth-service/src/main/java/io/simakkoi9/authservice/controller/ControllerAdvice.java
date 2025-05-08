package io.simakkoi9.authservice.controller;

import io.simakkoi9.authservice.exception.RoleNotFoundException;
import io.simakkoi9.authservice.exception.UserNotFoundException;
import io.simakkoi9.authservice.model.dto.client.response.ErrorResponse;
import io.simakkoi9.authservice.util.MessageKeyConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            UserNotFoundException.class,
            RoleNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationExceptions(ConstraintViolationException e) {
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
        } else if (e instanceof ConstraintViolationException) {
            ((ConstraintViolationException) e)
                    .getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(errors::add);

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
