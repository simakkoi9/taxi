package io.simakkoi9.ridesservice.model.dto.rest.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    LocalDateTime timestamp,

    int status,

    List<String> errors
) {}
