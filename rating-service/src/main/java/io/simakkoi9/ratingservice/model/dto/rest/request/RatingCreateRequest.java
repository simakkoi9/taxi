package io.simakkoi9.ratingservice.model.dto.rest.request;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record RatingCreateRequest(
    @NotBlank(message = "{ride.id.not.blank}")
    @JsonbProperty
    String rideId,

    @Min(value = 1, message = "{rate.driver.size}")
    @Max(value = 5, message = "{rate.driver.size}")
    @JsonbProperty
    Integer rateForDriver,

    @Min(value = 1, message = "{rate.passenger.size}")
    @Max(value = 5, message = "{rate.passenger.size}")
    @JsonbProperty
    Integer rateForPassenger,

    @Size(min = 1, max = 240, message = "{comment.driver.size}")
    @JsonbProperty
    String commentForDriver,

    @Size(min = 1, max = 240, message = "{comment.passenger.size}")
    @JsonbProperty
    String commentForPassenger

) implements Serializable {}
