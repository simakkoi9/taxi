package io.simakkoi9.ratingservice.model.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.simakkoi9.ratingservice.model.entity.RideStatus;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RideRequest(
    @JsonProperty
    String id,

    @JsonProperty
    RideStatus status
) implements Serializable {}

