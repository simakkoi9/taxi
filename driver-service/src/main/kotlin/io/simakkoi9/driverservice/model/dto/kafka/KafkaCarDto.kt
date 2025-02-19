package io.simakkoi9.driverservice.model.dto.kafka

import java.io.Serializable

data class KafkaCarDto(
    val id: Long,

    val brand: String,

    val model: String,

    val color: String,

    val number: String
) : Serializable

