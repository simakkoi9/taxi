package io.simakkoi9.driverservice.model.dto.rest.driver.response

import io.simakkoi9.driverservice.model.entity.Gender
import java.io.Serializable
import java.time.LocalDateTime

data class DriverResponse(
    val id: Long,

    val name: String,

    val email: String,

    val phone: String,

    val gender: Gender,

    val carId: Long?,

    val createdAt: LocalDateTime
) : Serializable