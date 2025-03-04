package io.simakkoi9.ratingservice.model.dto.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;

@JsonPropertyOrder({"personId", "averageRating"})
public record AverageRatingResponse(
    @JsonProperty
    String personId,

    @JsonProperty
    Double averageRating

) implements Serializable {}
