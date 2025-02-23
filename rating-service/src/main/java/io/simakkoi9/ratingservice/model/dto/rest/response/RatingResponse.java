package io.simakkoi9.ratingservice.model.dto.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;

@JsonPropertyOrder({"id", "rideId", "rateForDriver", "rateForPassenger", "commentForDriver", "commentForPassenger"})
public record RatingResponse(
    @JsonProperty
    Long id,

    @JsonProperty
    String rideId,

    @JsonProperty
    Integer rateForDriver,

    @JsonProperty
    Integer rateForPassenger,

    @JsonProperty
    String commentForDriver,

    @JsonProperty
    String commentForPassenger
) implements Serializable {}