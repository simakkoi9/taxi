package io.simakkoi9.ridesservice.service.kafka;

import io.simakkoi9.ridesservice.model.dto.kafka.KafkaDriverDto;
import io.simakkoi9.ridesservice.model.entity.Driver;
import io.simakkoi9.ridesservice.model.mapper.DriverMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final DriverMapper driverMapper;

    @KafkaListener(
            topics = "drivers-topic",
            groupId = "rides-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public Driver listenDriver(KafkaDriverDto kafkaDriverDto) {
        return driverMapper.toEntity(kafkaDriverDto);
    }

}
