package io.simakkoi9.ridesservice.model.dto.request;

import io.simakkoi9.ridesservice.model.entity.RideStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import static io.simakkoi9.ridesservice.util.RegularExpressionsConstants.ADDRESS_REGEX;

public record RideUpdateRequest(
    @Positive(message = "{passenger.id.positive}")
    Long passengerId,

    @Positive(message = "{driver.id.positive}")
    Long driverId,

    @Pattern(regexp = ADDRESS_REGEX, message = "{address.invalid}")
    String pickupAddress,

    @Pattern(regexp = ADDRESS_REGEX, message = "{address.invalid}")
    String destinationAddress,

    RideStatus status
) {}
