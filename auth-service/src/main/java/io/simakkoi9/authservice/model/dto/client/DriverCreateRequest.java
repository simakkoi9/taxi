package io.simakkoi9.authservice.model.dto.client;

import io.simakkoi9.authservice.model.Gender;

public record DriverCreateRequest(
       String externalId,

       String name,

       String email,

       String phone,

       Gender gender
) {}
