package io.simakkoi9.passengerservice.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record MultiErrorResponse(
    LocalDateTime timestamp,

    int status,

    List<String> errors
) {}
