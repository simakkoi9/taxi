package io.simakkoi9.authservice.model.dto.security.response;

import java.io.Serializable;
import java.time.LocalDateTime;

public record PassengerResponse(
        Long id,
        String name,
        String email,
        String phone,
        LocalDateTime createdAt
) implements Serializable {}
