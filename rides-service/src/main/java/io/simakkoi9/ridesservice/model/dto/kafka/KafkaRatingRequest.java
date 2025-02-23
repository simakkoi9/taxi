package io.simakkoi9.ridesservice.model.dto.kafka;

import java.io.Serializable;

public record KafkaRatingRequest(
    String key,

    Payload payload
) implements Serializable {}
