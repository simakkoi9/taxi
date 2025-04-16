package io.simakkoi9.authservice.model.dto.client;

import java.io.Serializable;

public record PassengerCreateRequest(
        String externalId,

        String name,

        String email,

        String phone
) implements Serializable {}
