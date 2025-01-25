package io.simakkoi9.passengerservice.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

import static io.simakkoi9.passengerservice.util.ValidationMessages.*;

public record PassengerCreateRequest(
        @NotBlank(message = BLANK_NAME)
        String name,
        @Email(regexp = EMAIL_REGEX, message = NOT_VALID_EMAIL)
        @NotBlank(message = BLANK_EMAIL)
        String email,
        @Pattern(regexp = PHONE_REGEX, message = NOT_VALID_PHONE)
        @NotBlank(message = BLANK_PHONE)
        String phone) implements Serializable {
}