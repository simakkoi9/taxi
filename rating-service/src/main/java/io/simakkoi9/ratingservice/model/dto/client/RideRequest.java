package io.simakkoi9.ratingservice.model.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.simakkoi9.ratingservice.model.entity.RideStatus;
import jakarta.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RideRequest(
    @JsonbProperty
    String id,

    @JsonbProperty
    RideStatus status
) implements Serializable {}

