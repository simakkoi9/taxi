package io.simakkoi9.ridesservice.model.dto.kafka;

import java.io.Serializable;

public record KafkaCarDto(
    Long id,

    String brand,

    String model,

    String color,

    String number
) implements Serializable {}
