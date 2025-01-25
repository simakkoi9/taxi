package io.simakkoi9.passengerservice.controller;


import io.simakkoi9.passengerservice.exception.DuplicateFoundException;
import io.simakkoi9.passengerservice.exception.ResourceNotFoundException;
import io.simakkoi9.passengerservice.model.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static io.simakkoi9.passengerservice.util.ErrorMessages.INTERNAL_SERVER_ERROR_MESSAGE;
import static io.simakkoi9.passengerservice.util.ErrorMessages.VALIDATION_FAILED_MESSAGE;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .timestamp(LocalDateTime.now())
                        .message(INTERNAL_SERVER_ERROR_MESSAGE + " " + e.getMessage())
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(DuplicateFoundException.class)
    public ResponseEntity<ErrorResponse> duplicateFoundException(DuplicateFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.CONFLICT.value())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .timestamp(LocalDateTime.now())
                        .message(VALIDATION_FAILED_MESSAGE + " " + e.getMessage())
                        .build());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> sqlException(SQLException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .timestamp(LocalDateTime.now())
                        .message(e.getMessage())
                        .build());
    }
}
