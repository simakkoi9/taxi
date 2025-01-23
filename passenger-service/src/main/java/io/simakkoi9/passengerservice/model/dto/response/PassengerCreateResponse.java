package io.simakkoi9.passengerservice.model.dto.response;

import io.simakkoi9.passengerservice.model.entity.UserStatus;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for {@link io.simakkoi9.passengerservice.model.entity.Passenger}
 */
public record PassengerCreateResponse(
        Long id,
        String name,
        String email,
        String phone,
        UserStatus status,
        Timestamp createdAt) implements Serializable {
}