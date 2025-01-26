package io.simakkoi9.driverservice.model.dto.car.request

import jakarta.validation.constraints.NotBlank
import java.io.Serializable

data class CarCreateRequest(

    @field:NotBlank
    val brand: String,

    @field:NotBlank
    val model: String,

    @field:NotBlank
    val color: String,

    @field:NotBlank
    val number: String

) : Serializable