package io.simakkoi9.driverservice.controller

import io.simakkoi9.driverservice.exception.CarIsNotAvailableException
import io.simakkoi9.driverservice.exception.CarNotFoundException
import io.simakkoi9.driverservice.exception.DriverNotFoundException
import io.simakkoi9.driverservice.exception.DuplicateCarFoundException
import io.simakkoi9.driverservice.exception.DuplicateDriverFoundException
import io.simakkoi9.driverservice.model.dto.ErrorResponse
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class ControllerAdvice(
    private val messageSource: MessageSource
) {
    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ErrorResponse> =
        buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e)

    @ExceptionHandler(CarIsNotAvailableException::class)
    fun handleCarIsNotAvailableException(e: CarIsNotAvailableException): ResponseEntity<ErrorResponse> =
        buildErrorResponse(HttpStatus.CONFLICT, e)

    @ExceptionHandler(
        CarNotFoundException::class,
        DriverNotFoundException::class
    )
    fun handleCarNotFoundException(e: RuntimeException): ResponseEntity<ErrorResponse> =
        buildErrorResponse(HttpStatus.NOT_FOUND, e)

    @ExceptionHandler(
        DuplicateCarFoundException::class,
        DuplicateDriverFoundException::class
    )
    fun handleDuplicateCarFoundException(e: RuntimeException): ResponseEntity<ErrorResponse> =
        buildErrorResponse(HttpStatus.BAD_REQUEST, e)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> =
        buildErrorResponse(HttpStatus.BAD_REQUEST, e)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseException(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> =
        buildErrorResponse(HttpStatus.BAD_REQUEST, e)

    private fun buildErrorResponse(status: HttpStatus, e: Exception): ResponseEntity<ErrorResponse> {
        val errors = mutableListOf<String?>()

        when (e) {
            is MethodArgumentNotValidException -> {
                e.bindingResult.allErrors.forEach {
                    errors.add(
                        it.defaultMessage ?: messageSource
                            .getMessage(
                                "validation.failed",
                                null,
                                LocaleContextHolder.getLocale()
                            )
                    )
                }
            }

            is HttpMessageNotReadableException -> {
                errors.add(e.message)
            }

            else -> {
                errors.add(
                    e.message ?: messageSource
                        .getMessage(
                            "internal.server.error",
                            null,
                            LocaleContextHolder.getLocale()
                        )
                )
            }
        }

        return ResponseEntity.status(status).body(
            ErrorResponse(
                status = status.value(),
                timestamp = LocalDateTime.now(),
                errors = errors
            )
        )
    }

}