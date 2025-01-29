package io.simakkoi9.driverservice.model.dto

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime,

    val status: Int,

    val message: String
)

