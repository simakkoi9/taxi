package io.simakkoi9.driverservice.model.dto.driver.request

import io.simakkoi9.driverservice.model.entity.Gender
import io.simakkoi9.driverservice.util.RegularExpressionsConstants.PHONE_REGEX
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable

data class DriverUpdateRequest(
    @field:Size(max = 70, message = "{name.size}")
    val name: String? = null,

    @field:Email(message = "{email.invalid}")
    @field:Size(max = 320, message = "{email.size}")
    val email: String? = null,

    @field:Pattern(regexp = PHONE_REGEX, message = "{phone.invalid}")
    @field:Size(max = 20, message = "{phone.size}")
    val phone: String? = null,

    val gender: Gender? = null,
) : Serializable