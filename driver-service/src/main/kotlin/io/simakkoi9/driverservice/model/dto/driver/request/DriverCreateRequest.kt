package io.simakkoi9.driverservice.model.dto.driver.request

import io.simakkoi9.driverservice.model.entity.Gender
import io.simakkoi9.driverservice.util.RegularExpressionsConstants.NAME_REGEX
import io.simakkoi9.driverservice.util.RegularExpressionsConstants.PHONE_REGEX
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable

data class DriverCreateRequest(
    @field:NotBlank(message = "{name.not-blank}")
    @field:Pattern(regexp = NAME_REGEX, message = "{name.invalid}")
    @field:Size(max = 70, message = "{name.size}")
    val name: String,

    @field:Email(message = "{email.invalid}")
    @field:NotBlank(message = "{email.not-blank}")
    @field:Size(max = 320, message = "{email.not-valid}")
    val email: String,

    @field:Pattern(regexp = PHONE_REGEX, message = "{phone.invalid}")
    @field:NotBlank(message = "{phone.not-blank}")
    @field:Size(max = 20, message = "{phone.size}")
    val phone: String,

    @field:NotNull(message = "{gender.not-null}")
    val gender: Gender,
) : Serializable