package io.simakkoi9.ratingservice.model.dto.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record DriverRatingUpdateRequest(
    @NotNull
    @Min(value = 1, message = "{rate.driver.size}")
    @Max(value = 5, message = "{rate.driver.size}")
    @JsonProperty("rate")
    Integer rateForDriver,

    @Size(min = 1, max = 240, message = "{comment.driver.size}")
    @JsonProperty("comment")
    String commentForDriver

) implements Serializable {}
