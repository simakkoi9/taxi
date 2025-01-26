package io.simakkoi9.driverservice.model.dto.car.response

import java.io.Serializable

data class CarResponse(

    val id: Long,

    val brand: String,

    val model: String,

    val color: String,

    val number: String

) : Serializable