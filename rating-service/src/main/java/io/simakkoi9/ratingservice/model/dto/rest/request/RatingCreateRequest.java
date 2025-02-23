package io.simakkoi9.ratingservice.model.dto.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record RatingCreateRequest(
    @NotBlank(message = "{ride.id.not.blank}")
    @JsonProperty
    String rideId,

    @Min(value = 1, message = "{rate.driver.size}")
    @Max(value = 5, message = "{rate.driver.size}")
    @JsonProperty
    Integer rateForDriver,

    @Min(value = 1, message = "{rate.passenger.size}")
    @Max(value = 5, message = "{rate.passenger.size}")
    @JsonProperty
    Integer rateForPassenger,

    @Size(min = 1, max = 240, message = "{comment.driver.size}")
    @JsonProperty
    String commentForDriver,

    @Size(min = 1, max = 240, message = "{comment.passenger.size}")
    @JsonProperty
    String commentForPassenger

) implements Serializable {}
