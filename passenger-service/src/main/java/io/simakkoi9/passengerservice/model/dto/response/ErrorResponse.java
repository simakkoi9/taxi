package io.simakkoi9.passengerservice.model.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message
) implements Serializable {}
