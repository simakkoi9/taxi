package io.simakkoi9.ridesservice.service.kafka;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, List<Long>> kafkaTemplate;

    public void sendDriverIdList(String rideId, List<Long> driverIdList) {
        kafkaTemplate.send("drivers-topic", rideId, driverIdList);
    }

}
