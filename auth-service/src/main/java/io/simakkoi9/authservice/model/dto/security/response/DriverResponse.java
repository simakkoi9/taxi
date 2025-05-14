package io.simakkoi9.authservice.model.dto.security.response;

import io.simakkoi9.authservice.model.Gender;
import java.time.LocalDateTime;

public record DriverResponse(
        Long id,
        String name,
        String email,
        String phone,
        Gender gender,
        Long carId,
        LocalDateTime createdAt
) {}
