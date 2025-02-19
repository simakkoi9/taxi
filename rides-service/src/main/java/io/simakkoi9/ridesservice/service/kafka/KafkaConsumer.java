package io.simakkoi9.ridesservice.service.kafka;

import io.simakkoi9.ridesservice.model.dto.kafka.KafkaDriverRequest;
import io.simakkoi9.ridesservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final RideService rideService;

    @KafkaListener(
            topics = "rides-topic",
            groupId = "rides-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenDriver(ConsumerRecord<String, KafkaDriverRequest> record) {
        rideService.handleAvailableDriver(record.key(), record.value());
    }

}
