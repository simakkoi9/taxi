package io.simakkoi9.ratingservice.model.dto.request;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record RatingCreateRequest(
    @NotBlank(message = "{ride.id.not.blank}")
    @JsonbProperty
    String rideId,

    @Max(value = 5, message = "{rate.driver.size}")
    @JsonbProperty
    Integer rateForDriver,

    @Max(value = 5, message = "{rate.passenger.size}")
    @JsonbProperty
    Integer rateForPassenger,

    @Size(max = 240, message = "{comment.driver.size}")
    @JsonbProperty
    String commentForDriver,

    @Size(max = 240, message = "{comment.passenger.size}")
    @JsonbProperty
    String commentForPassenger

) implements Serializable {}
