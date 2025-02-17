package io.simakkoi9.passengerservice.model.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MultiErrorResponse(
    LocalDateTime timestamp,

    int status,

    List<String> errors
) {}
