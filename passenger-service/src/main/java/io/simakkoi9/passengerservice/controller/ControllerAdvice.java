package io.simakkoi9.passengerservice.controller;

import io.simakkoi9.passengerservice.exception.DuplicatePassengerFoundException;
import io.simakkoi9.passengerservice.exception.PassengerNotFoundException;
import io.simakkoi9.passengerservice.model.dto.response.ErrorResponse;
import io.simakkoi9.passengerservice.model.dto.response.MultiErrorResponse;
import java.sql.SQLException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .timestamp(LocalDateTime.now())
                        .message(
                                messageSource.getMessage(
                                    "internal.server.error",
                                    new Object[]{},
                                    LocaleContextHolder.getLocale()
                                )
                        )
                        .build()
                );
    }

    @ExceptionHandler(PassengerNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(PassengerNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(DuplicatePassengerFoundException.class)
    public ResponseEntity<ErrorResponse> duplicateFoundException(DuplicatePassengerFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.CONFLICT.value())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MultiErrorResponse> validationException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MultiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errors(
                                e.getBindingResult()
                                .getAllErrors()
                                .stream()
                                .map(ObjectError::getDefaultMessage)
                                .toList()
                        )
                        .build()
                );
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> sqlException(SQLException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
                        .build()
                );
    }
}
