package io.simakkoi9.passengerservice.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

import static io.simakkoi9.passengerservice.util.ValidationMessages.*;


public record PassengerRequest(
        @Email(regexp = EMAIL_REGEX, message = NOT_VALID_EMAIL)
        @NotBlank(message = BLANK_EMAIL)
        String email) implements Serializable {
}