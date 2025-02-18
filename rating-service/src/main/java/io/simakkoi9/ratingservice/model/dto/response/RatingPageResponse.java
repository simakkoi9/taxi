package io.simakkoi9.ratingservice.model.dto.response;

import jakarta.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;

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
