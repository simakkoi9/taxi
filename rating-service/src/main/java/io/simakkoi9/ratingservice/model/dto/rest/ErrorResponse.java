package io.simakkoi9.ratingservice.model.dto.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.time.LocalDateTime;

@JsonPropertyOrder({"timestamp", "status", "message"})
public record ErrorResponse(
    @JsonProperty
    LocalDateTime timestamp,

    @JsonProperty
    int status,

    @JsonProperty
    String message

) implements Serializable {}
