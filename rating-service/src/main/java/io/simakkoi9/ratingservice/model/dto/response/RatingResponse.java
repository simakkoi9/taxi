package io.simakkoi9.ratingservice.model.dto.response;

import java.io.Serializable;

public record RatingResponse(
        Long id,

        String rideId,

        Integer rateForDriver,

        Integer rateForPassenger,

        String commentForDriver,

        String commentForPassenger

) implements Serializable {}