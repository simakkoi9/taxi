package io.simakkoi9.ratingservice.model.dto.kafka;

import java.io.Serializable;

public record RideListRequest(
    String personId,

    int limit
) implements Serializable {}
