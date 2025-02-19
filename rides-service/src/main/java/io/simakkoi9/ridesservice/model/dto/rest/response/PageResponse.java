package io.simakkoi9.ridesservice.model.dto.rest.response;

import java.util.List;

public record PageResponse<T>(
    List<T> content,

    int size,

    int page,

    int totalPages,

    long totalElements
) {}
