package io.simakkoi9.passengerservice.model.dto.request;

import static io.simakkoi9.passengerservice.util.RegularExpressionConstants.NAME_REGEX;
import static io.simakkoi9.passengerservice.util.RegularExpressionConstants.PHONE_REGEX;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record PassengerCreateRequest(
        String externalId,

        @NotBlank(message = "{name.blank}")
        @Pattern(regexp = NAME_REGEX, message = "{name.not-valid}")
        String name,
        @Email(message = "{email.not-valid}")
        @NotBlank(message = "{email.blank}")
        @Size(max = 320, message = "{email.not-valid}")
        String email,
        @Pattern(regexp = PHONE_REGEX, message = "{phone.not-valid}")
        @NotBlank(message = "{phone.blank}")
        String phone
) implements Serializable {}