package io.simakkoi9.ridesservice.service.kafka;

import io.simakkoi9.ridesservice.exception.AvailableDriverProcessingException;
import io.simakkoi9.ridesservice.exception.NoAvailableDriversException;
import io.simakkoi9.ridesservice.model.dto.kafka.KafkaDriverRequest;
import io.simakkoi9.ridesservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.MessageSource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final RideService rideService;
    private final MessageSource messageSource;

    @KafkaListener(
            topics = "rides-topic",
            groupId = "rides-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenDriver(ConsumerRecord<String, KafkaDriverRequest> record) {
        try {
            rideService.handleAvailableDriver(record.key(), record.value());
        } catch (RuntimeException e) {
            throw new AvailableDriverProcessingException("", messageSource, record.key());
        }
    }

    @KafkaListener(
        topics = "rides-error-topic",
        groupId = "error-group",
        containerFactory = "kafkaErrorListenerContainerFactory"
    )
    public void listenError(ConsumerRecord<String, String> record) {
        throw new NoAvailableDriversException(record.value(), messageSource, record.key());
    }

}
