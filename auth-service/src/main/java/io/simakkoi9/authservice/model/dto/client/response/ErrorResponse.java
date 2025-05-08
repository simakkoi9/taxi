package io.simakkoi9.authservice.model.dto.client.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,

        int status,

        List<String> errors
) {}
