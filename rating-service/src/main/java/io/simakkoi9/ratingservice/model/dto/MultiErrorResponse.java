package io.simakkoi9.ratingservice.model.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.List;

public record MultiErrorResponse(
        @JsonbProperty(value = "errors")
      List<ErrorResponse> errorResponseList

) implements Serializable {}
