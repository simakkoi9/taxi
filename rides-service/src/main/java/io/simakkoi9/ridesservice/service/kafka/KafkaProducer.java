package io.simakkoi9.ridesservice.service.kafka;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, List<Long>> kafkaTemplate;
    private final KafkaTemplate<String, String> ratingKafkaTemplate;

    public void sendDriverIdList(String rideId, List<Long> driverIdList) {
        kafkaTemplate.send("drivers-topic", rideId, driverIdList);
    }

    public void sendPersonId(String personId, String rate) {
        ratingKafkaTemplate.send("person-topic", personId, rate);
    }

}
