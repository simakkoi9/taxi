package io.simakkoi9.ratingservice.model.dto.response;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record RatingPageResponse(
    @JsonbProperty
    List<RatingResponse> content,

    @JsonbProperty
    int currentPage,

    @JsonbProperty
    int size,

    @JsonbProperty
    int totalPages
) implements Serializable {}
