package io.simakkoi9.ratingservice.model.dto.response;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.io.Serializable;

@JsonbPropertyOrder({"personId", "averageRating"})
public record AverageRatingResponse(
    @JsonbProperty
    Long personId,

    @JsonbProperty
    Double averageRating

) implements Serializable {}
