package io.simakkoi9.ratingservice.model.dto.rest.response;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.io.Serializable;

@JsonbPropertyOrder({"id", "rideId", "rateForDriver", "rateForPassenger", "commentForDriver", "commentForPassenger"})
public record RatingResponse(
    @JsonbProperty
    Long id,

    @JsonbProperty
    String rideId,

    @JsonbProperty
    Integer rateForDriver,

    @JsonbProperty
    Integer rateForPassenger,

    @JsonbProperty
    String commentForDriver,

    @JsonbProperty
    String commentForPassenger

) implements Serializable {}