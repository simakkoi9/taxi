package io.simakkoi9.passengerservice.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;


public record PassengerRequest(
        @Email
        @NotBlank
        String email) implements Serializable {
}