package io.simakkoi9.ridesservice.model.dto.request;

import java.io.Serializable;

public record RideCreateRequest(
    Long passengerId,

    String pickupAddress,

    String destinationAddress
) implements Serializable {}
