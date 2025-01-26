package io.simakkoi9.passengerservice.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

import static io.simakkoi9.passengerservice.util.ValidationMessages.*;


public record PassengerUpdateRequest(
        String name,
        @Email(message = NOT_VALID_EMAIL)
        String email,
        @Pattern(regexp = PHONE_REGEX, message = NOT_VALID_PHONE)
        String phone) implements Serializable {}