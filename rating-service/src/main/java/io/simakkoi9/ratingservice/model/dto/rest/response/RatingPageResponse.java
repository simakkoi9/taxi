package io.simakkoi9.ratingservice.model.dto.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;

@Builder
public record RatingPageResponse(
    @JsonProperty
    List<RatingResponse> content,

    @JsonProperty
    int currentPage,

    @JsonProperty
    int size,

    @JsonProperty
    int totalPages
) implements Serializable {}
