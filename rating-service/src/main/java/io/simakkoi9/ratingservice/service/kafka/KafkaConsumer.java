package io.simakkoi9.ratingservice.service.kafka;

import io.simakkoi9.ratingservice.model.entity.Rate;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class KafkaConsumer {
    @Incoming("get-person")
    @Transactional
    public Uni<Void> listen(Record<String, String> record) {
        Rate rate = new Rate();
        rate.setPersonId(record.key());
        rate.setRate(Integer.valueOf(record.value()));
        rate.persist();
        return Uni.createFrom().voidItem();
    }
}
