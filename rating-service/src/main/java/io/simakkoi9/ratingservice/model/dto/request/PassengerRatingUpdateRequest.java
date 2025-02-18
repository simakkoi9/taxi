package io.simakkoi9.ratingservice.model.dto.request;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record PassengerRatingUpdateRequest(
    @NotNull
    @Min(value = 1, message = "{rate.passenger.size}")
    @Max(value = 5, message = "{rate.passenger.size}")
    @JsonbProperty("rate")
    Integer rateForPassenger,

    @Size(min = 1, max = 240, message = "{comment.passenger.size}")
    @JsonbProperty("comment")
    String commentForPassenger

) implements Serializable {}
