package io.simakkoi9.ridesservice.model.dto.response;

import io.simakkoi9.ridesservice.model.entity.Driver;
import io.simakkoi9.ridesservice.model.entity.Passenger;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideResponse(
    String id,

    Passenger passenger,

    Driver driver,

    String pickupAddress,

    String destinationAddress,

    BigDecimal cost,

    RideStatus status,

    LocalDateTime orderDateTime
) implements Serializable {}
