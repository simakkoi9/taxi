package io.simakkoi9.driverservice.model.dto.kafka

import io.simakkoi9.driverservice.model.entity.Gender

data class KafkaDriverDto(
    val id: Long,

    val name: String,

    val email: String,

    val phone: String,

    val gender: Gender,

    val car: KafkaCarDto
)