package io.simakkoi9.ridesservice.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Long> kafkaTemplate;

    public void sendDriverId(Long driverId) {
        kafkaTemplate.send("drivers-topic", driverId);
    }

}
