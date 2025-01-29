package io.simakkoi9.driverservice.util

object ErrorMessages {
    const val VALIDATION_FAILED_MESSAGE = "Validation failed."

    const val INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected error occurred."

    const val DRIVER_NOT_FOUND_MESSAGE = "Driver %d not found."

    const val DUPLICATE_DRIVER_FOUND_MESSAGE = "Driver with email %s already exists."

    const val CAR_NOT_FOUND_MESSAGE = "Car %d not found."

    const val DUPLICATE_CAR_FOUND_MESSAGE = "Car with number %s already exists."

    const val CAR_IS_NOT_AVAILABLE_MESSAGE = "Car is being used by another driver."

    const val UNKNOWN_GENDER_VALUE_MESSAGE = "Unknown gender status: %s"

    const val UNKNOWN_GENDER_CODE_MESSAGE = "Unknown gender type code: %d"

    const val UNKNOWN_STATUS_VALUE_MESSAGE = "Unknown entry status value: %s"

    const val UNKNOWN_STATUS_CODE_MESSAGE = "Unknown entry status code: %d"
}
