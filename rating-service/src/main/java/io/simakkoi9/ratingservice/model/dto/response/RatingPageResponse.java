package io.simakkoi9.ratingservice.model.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record RatingPageResponse(
      List<RatingResponse> content,

      int currentPage,

      int size,

      int totalPages
) {}
