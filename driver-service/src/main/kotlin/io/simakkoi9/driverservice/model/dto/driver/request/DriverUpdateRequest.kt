package io.simakkoi9.driverservice.model.dto.driver.request

import io.simakkoi9.driverservice.model.entity.Gender
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import java.io.Serializable

data class DriverUpdateRequest(
    val name: String? = null,

    @field:Email
    val email: String? = null,

    @field:Pattern(regexp = "")
    val phone: String? = null,

    val gender: Gender? = null,

    @Positive
    val carId: Long? = null
) : Serializable