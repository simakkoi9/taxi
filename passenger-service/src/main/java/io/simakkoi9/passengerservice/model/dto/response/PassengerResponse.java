package io.simakkoi9.passengerservice.model.dto.response;

import java.io.Serializable;
import java.sql.Timestamp;

public record PassengerResponse(
        Long id,
        String name,
        String email,
        String phone,
        String status,
        Timestamp createdAt) implements Serializable {
}