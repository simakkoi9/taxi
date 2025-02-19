package io.simakkoi9.ridesservice.model.dto.rest.request;

import static io.simakkoi9.ridesservice.util.RegularExpressionsConstants.ADDRESS_REGEX;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;

public record RideCreateRequest(
    @NotNull(message = "{passenger.id.not-blank}")
    @Positive(message = "{passenger.id.positive}")
    Long passengerId,

    @NotBlank(message = "{address.not-blank}")
    @Pattern(regexp = ADDRESS_REGEX, message = "{address.invalid}")
    String pickupAddress,

    @NotBlank(message = "{address.not-blank}")
    @Pattern(regexp = ADDRESS_REGEX, message = "{address.invalid}")
    String destinationAddress
) implements Serializable {}
