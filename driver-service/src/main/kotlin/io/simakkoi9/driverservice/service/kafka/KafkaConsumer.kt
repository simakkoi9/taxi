package io.simakkoi9.driverservice.service.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaConsumer {

    @KafkaListener(
        topics = ["drivers-topic"],
        groupId = "drivers-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listenDriverId(driverId: Long): Long = driverId

}