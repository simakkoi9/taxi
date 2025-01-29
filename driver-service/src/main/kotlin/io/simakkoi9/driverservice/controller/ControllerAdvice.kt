package io.simakkoi9.driverservice.controller

import io.simakkoi9.driverservice.exception.CarIsNotAvailableException
import io.simakkoi9.driverservice.exception.CarNotFoundException
import io.simakkoi9.driverservice.exception.DriverNotFoundException
import io.simakkoi9.driverservice.exception.DuplicateCarFoundException
import io.simakkoi9.driverservice.exception.DuplicateDriverFoundException
import io.simakkoi9.driverservice.exception.GenderValidationException
import io.simakkoi9.driverservice.model.dto.ErrorResponse
import io.simakkoi9.driverservice.util.ErrorMessages.CAR_IS_NOT_AVAILABLE_MESSAGE
import io.simakkoi9.driverservice.util.ErrorMessages.CAR_NOT_FOUND_MESSAGE
import io.simakkoi9.driverservice.util.ErrorMessages.DRIVER_NOT_FOUND_MESSAGE
import io.simakkoi9.driverservice.util.ErrorMessages.DUPLICATE_CAR_FOUND_MESSAGE
import io.simakkoi9.driverservice.util.ErrorMessages.DUPLICATE_DRIVER_FOUND_MESSAGE
import io.simakkoi9.driverservice.util.ErrorMessages.INTERNAL_SERVER_ERROR_MESSAGE
import io.simakkoi9.driverservice.util.ErrorMessages.UNKNOWN_GENDER_VALUE_MESSAGE
import io.simakkoi9.driverservice.util.ErrorMessages.VALIDATION_FAILED_MESSAGE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class ControllerAdvice {
    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception) : ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    timestamp = LocalDateTime.now(),
                    message = "$INTERNAL_SERVER_ERROR_MESSAGE ${e.message}"
                )
            )
    }

    @ExceptionHandler(CarIsNotAvailableException::class)
    fun handleCarIsNotAvailableException(e: CarIsNotAvailableException) : ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ErrorResponse(
                    status = HttpStatus.CONFLICT.value(),
                    timestamp = LocalDateTime.now(),
                    message = "$CAR_IS_NOT_AVAILABLE_MESSAGE ${e.message}"
                )
            )
    }

    @ExceptionHandler(CarNotFoundException::class)
    fun handleCarNotFoundException(e: CarNotFoundException) : ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    status = HttpStatus.NOT_FOUND.value(),
                    timestamp = LocalDateTime.now(),
                    message = "$CAR_NOT_FOUND_MESSAGE ${e.message}"
                )
            )
    }

    @ExceptionHandler(DriverNotFoundException::class)
    fun handleDriverNotFoundException(e: DriverNotFoundException) : ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    status = HttpStatus.NOT_FOUND.value(),
                    timestamp = LocalDateTime.now(),
                    message = "$DRIVER_NOT_FOUND_MESSAGE ${e.message}"
                )
            )
    }

    @ExceptionHandler(DuplicateCarFoundException::class)
    fun handleDuplicateCarFoundException(e: DuplicateCarFoundException) : ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    timestamp = LocalDateTime.now(),
                    message = "$DUPLICATE_CAR_FOUND_MESSAGE ${e.message}"
                )
            )
    }

    @ExceptionHandler(DuplicateDriverFoundException::class)
    fun handleDuplicateDriverFoundException(e: DuplicateDriverFoundException) : ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    timestamp = LocalDateTime.now(),
                    message = "$DUPLICATE_DRIVER_FOUND_MESSAGE ${e.message}"
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException) : ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    timestamp = LocalDateTime.now(),
                    message = "$VALIDATION_FAILED_MESSAGE ${e.message}"
                )
            )
    }

    @ExceptionHandler(GenderValidationException::class)
    fun handleGenderValidationException(e: GenderValidationException ) : ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    timestamp = LocalDateTime.now(),
                    message = "$UNKNOWN_GENDER_VALUE_MESSAGE ${e.message}"
                )
            )
    }

}