package io.simakkoi9.driverservice.model.dto.rest.car.request

import io.simakkoi9.driverservice.util.RegularExpressionsConstants.CAR_NUMBER_REGEX
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable

data class CarCreateRequest(
    @field:NotBlank(message = "{brand.not-blank}")
    @field:Size(max = 25, message = "{brand.size}")
    val brand: String,

    @field:NotBlank(message = "{model.not-blank}")
    @field:Size(max = 40, message = "{model.size}")
    val model: String,

    @field:NotBlank(message = "{color.not-blank}")
    @field:Size(max = 40, message = "{color.size}")
    val color: String,

    @field:Pattern(regexp = CAR_NUMBER_REGEX, message = "{number.invalid}")
    @field:Size(max = 15, message = "{color.size}")
    val number: String
) : Serializable