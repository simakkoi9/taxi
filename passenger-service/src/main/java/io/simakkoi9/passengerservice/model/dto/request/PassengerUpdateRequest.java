package io.simakkoi9.passengerservice.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

/**
 * DTO for {@link io.simakkoi9.passengerservice.model.entity.Passenger}
 */
public record PassengerUpdateRequest(
        @NotBlank
        String name,
        @Email
        @NotBlank
        String email,
        @Pattern(regexp = "^\\+375(25|29|33|44|17)\\d{7}$|^80(25|29|33|44|17)\\d{7}$")
        @NotBlank
        String phone) implements Serializable {
}