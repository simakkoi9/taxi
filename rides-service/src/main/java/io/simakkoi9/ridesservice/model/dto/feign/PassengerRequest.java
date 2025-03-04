package io.simakkoi9.ridesservice.model.dto.feign;

import java.io.Serializable;

public record PassengerRequest(
    Long id,

    String name,

    String email,

    String phone
) implements Serializable {}
