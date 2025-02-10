package io.simakkoi9.ratingservice.model.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;

import java.time.LocalDateTime;

@JsonbPropertyOrder({"timestamp", "status", "message"})
public record ErrorResponse(
    @JsonbProperty
    LocalDateTime timestamp,

    @JsonbProperty
    int status,

    @JsonbProperty
    String message
) {}
