package io.simakkoi9.ratingservice.model.dto.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public record MultiErrorResponse(
      @JsonProperty(value = "errors")
      List<ErrorResponse> errorResponseList

) implements Serializable {}
