package io.simakkoi9.driverservice.service.kafka

import io.simakkoi9.driverservice.model.dto.kafka.KafkaDriverDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    fun sendDriver(rideId: String, kafkaDriverDto: KafkaDriverDto) {
        kafkaTemplate.send("rides-topic", rideId, kafkaDriverDto)
    }

}