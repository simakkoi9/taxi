package io.simakkoi9.driverservice.service.kafka

import io.simakkoi9.driverservice.model.dto.kafka.KafkaDriverResponse
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, KafkaDriverResponse>
) {

    fun sendDriver(rideId: String, kafkaDriverResponse: KafkaDriverResponse) {
        kafkaTemplate.send("rides-topic", rideId, kafkaDriverResponse)
    }

}