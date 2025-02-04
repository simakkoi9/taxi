package io.simakkoi9.ratingservice.model.dto.request;

import java.io.Serializable;

public record DriverRatingUpdateRequest(
    Integer rateForDriver,

    String commentForDriver

) implements Serializable {}
