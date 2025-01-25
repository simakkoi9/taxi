package io.simakkoi9.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {
    public static final String VALIDATION_FAILED_MESSAGE = "Validation failed.";

    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected error occurred.";

    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource %s not found.";

    public static final String DUPLICATE_FOUND_MESSAGE = "Passenger with email %s already exists.";

}
