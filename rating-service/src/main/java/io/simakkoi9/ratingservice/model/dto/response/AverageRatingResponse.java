package io.simakkoi9.ratingservice.model.dto.response;

import java.io.Serializable;

public record AverageRatingResponse(
    Long personId,

    Double averageRating

) implements Serializable {}
