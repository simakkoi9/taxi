package io.simakkoi9.driverservice.model.dto.car.request

import io.simakkoi9.driverservice.util.RegularExpressionsConstants
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable

data class CarUpdateRequest(
    @field:Size(max = 25, message = "{brand.size}")
    val brand: String? = null,

    @field:Size(max = 40, message = "{model.size}")
    val model: String? = null,

    @field:Size(max = 40, message = "{color.size}")
    val color: String? = null,

    @field:Pattern(regexp = RegularExpressionsConstants.CAR_NUMBER_REGEX, message = "{number.invalid}")
    @field:Size(max = 15, message = "{color.size}")
    val number: String? = null
) : Serializable