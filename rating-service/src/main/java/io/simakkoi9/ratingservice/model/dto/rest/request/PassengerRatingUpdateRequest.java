package io.simakkoi9.ratingservice.model.dto.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record PassengerRatingUpdateRequest(
    @NotNull
    @Min(value = 1, message = "{rate.passenger.size}")
    @Max(value = 5, message = "{rate.passenger.size}")
    @JsonProperty("rate")
    Integer rateForPassenger,

    @Size(min = 1, max = 240, message = "{comment.passenger.size}")
    @JsonProperty("comment")
    String commentForPassenger

) implements Serializable {}
