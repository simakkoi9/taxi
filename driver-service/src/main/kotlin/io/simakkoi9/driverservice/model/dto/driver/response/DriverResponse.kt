package io.simakkoi9.driverservice.model.dto.driver.response

import io.simakkoi9.driverservice.model.entity.Gender
import java.io.Serializable

data class DriverResponse(
    val id: Long,

    val name: String,

    val email: String,

    val phone: String,

    val gender: Gender,

    val carId: Long
) : Serializable