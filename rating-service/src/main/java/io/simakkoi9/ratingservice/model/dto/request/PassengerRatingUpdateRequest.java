package io.simakkoi9.ratingservice.model.dto.request;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record PassengerRatingUpdateRequest(
    @NotNull
    @Max(value = 5, message = "{rate.driver.size}")
    @JsonbProperty("rate")
    Integer rateForPassenger,

    @Size(max = 240, message = "{comment.passenger.size}")
    @JsonbProperty("comment")
    String commentForPassenger

) implements Serializable {}
