package io.simakkoi9.passengerservice.model.dto.response;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record ErrorResponse(
        Timestamp timestamp,
        int status,
        String message
) {

}
