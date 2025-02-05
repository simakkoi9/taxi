package io.simakkoi9.ratingservice.model.dto.request;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record DriverRatingUpdateRequest(
    @NotNull
    @Max(value = 5, message = "{rate.driver.size}")
    @JsonbProperty("rate")
    Integer rateForDriver,

    @Size(max = 240, message = "{comment.driver.size}")
    @JsonbProperty("comment")
    String commentForDriver

) implements Serializable {}
