package io.simakkoi9.driverservice.model.dto.driver.request

import io.simakkoi9.driverservice.model.entity.Gender
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.io.Serializable

data class DriverCreateRequest(
    @field:NotBlank
    val name: String,

    @field:Email
    @field:NotBlank
    val email: String,

//    @field:Pattern(regexp = "")
    @field:NotBlank
    val phone: String,

    @field:NotNull
    val gender: Gender,
) : Serializable