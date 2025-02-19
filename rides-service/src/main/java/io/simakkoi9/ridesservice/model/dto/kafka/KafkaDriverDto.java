package io.simakkoi9.ridesservice.model.dto.kafka;

import io.simakkoi9.ridesservice.model.entity.Gender;
import java.io.Serializable;

public record KafkaDriverDto(
    Long id,

    String name,

    String email,

    String phone,

    Gender gender,

    KafkaCarDto car
) implements Serializable {}
