package io.simakkoi9.driverservice.service.kafka

import io.simakkoi9.driverservice.service.DriverService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaConsumer(
    private val driverService: DriverService,
    private val kafkaProducer: KafkaProducer
) {

    @KafkaListener(
        topics = ["drivers-topic"],
        groupId = "drivers-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listenDriverIdList(record: ConsumerRecord<String, List<Long>>) {
        val driverDto = driverService.getAvailableDriverForRide(record.value())
        kafkaProducer.sendDriver(record.key(), driverDto)
    }

}