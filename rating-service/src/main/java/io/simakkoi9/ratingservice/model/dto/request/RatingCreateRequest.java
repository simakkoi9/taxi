package io.simakkoi9.ratingservice.model.dto.request;

import java.io.Serializable;

public record RatingCreateRequest(
    String rideId,

    Integer rateForDriver,

    Integer rateForPassenger,

    String commentForDriver,

    String commentForPassenger

) implements Serializable {}
