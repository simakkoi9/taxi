package io.simakkoi9.passengerservice.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

public record PassengerCreateRequest(
        @NotBlank
        String name,
        @Email(regexp = "^(.+)@(\\S+)$")
        @NotBlank
        String email,
        @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$")
        @NotBlank
        String phone) implements Serializable {
}