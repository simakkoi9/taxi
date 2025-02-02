package io.simakkoi9.ridesservice.model.dto.request;

import io.simakkoi9.ridesservice.model.entity.RideStatus;

public record RideUpdateRequest(
    Long passengerId,

    Long driverId,

    String pickupAddress,

    String destinationAddress,

    RideStatus status
) {}
