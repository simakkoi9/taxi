package io.simakkoi9.ratingservice.model.dto.request;

import java.io.Serializable;

public record PassengerRatingUpdateRequest(
    Integer rateForPassenger,

    String commentForPassenger

) implements Serializable {}
