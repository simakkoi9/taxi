package io.simakkoi9.ridesservice.model.dto.rest.request;

import static io.simakkoi9.ridesservice.util.RegularExpressionsConstants.ADDRESS_REGEX;

import jakarta.validation.constraints.Pattern;

public record RideUpdateRequest(
    @Pattern(regexp = ADDRESS_REGEX, message = "{address.invalid}")
    String pickupAddress,

    @Pattern(regexp = ADDRESS_REGEX, message = "{address.invalid}")
    String destinationAddress
) {}
