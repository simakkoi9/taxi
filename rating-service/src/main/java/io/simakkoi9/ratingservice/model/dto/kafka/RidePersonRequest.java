package io.simakkoi9.ratingservice.model.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public record RidePersonRequest(
    @JsonProperty
    String person,

    @JsonProperty
    Integer rate
) implements Serializable {}
