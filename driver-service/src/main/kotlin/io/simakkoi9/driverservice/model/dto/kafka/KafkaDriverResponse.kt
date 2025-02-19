package io.simakkoi9.driverservice.model.dto.kafka

import io.simakkoi9.driverservice.model.entity.Gender
import java.io.Serializable

data class KafkaDriverResponse(
    val id: Long,

    val name: String,

    val email: String,

    val phone: String,

    val gender: Gender,

    val car: KafkaCarDto
): Serializable