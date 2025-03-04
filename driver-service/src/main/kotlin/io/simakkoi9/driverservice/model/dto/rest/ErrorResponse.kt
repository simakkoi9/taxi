package io.simakkoi9.driverservice.model.dto.rest

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime,

    val status: Int,

    val errors: List<String?>
)

