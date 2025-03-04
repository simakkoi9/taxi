package io.simakkoi9.ridesservice.model.dto.kafka;

import java.io.Serializable;

public record Payload(
    String person,

    Integer rate
) implements Serializable {}
