package io.simakkoi9.driverservice.model.dto.rest.car.response

import java.io.Serializable
import java.time.LocalDateTime

data class CarResponse(
    val id: Long,

    val brand: String,

    val model: String,

    val color: String,

    val number: String,

    val createdAt: LocalDateTime
) : Serializable