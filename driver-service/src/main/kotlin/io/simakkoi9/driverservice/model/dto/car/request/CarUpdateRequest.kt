package io.simakkoi9.driverservice.model.dto.car.request

import java.io.Serializable

data class CarUpdateRequest(
    val brand: String? = null,

    val model: String? = null,

    val color: String? = null,

    val number: String? = null
) : Serializable