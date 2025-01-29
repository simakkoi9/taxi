package io.simakkoi9.driverservice.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.simakkoi9.driverservice.exception.GenderValidationException
import io.simakkoi9.driverservice.util.ErrorMessages.UNKNOWN_GENDER_CODE_MESSAGE
import io.simakkoi9.driverservice.util.ErrorMessages.UNKNOWN_GENDER_VALUE_MESSAGE

enum class Gender(val code: Int) {
    MALE(1),
    FEMALE(2);

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): Gender {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: throw GenderValidationException(UNKNOWN_GENDER_VALUE_MESSAGE.format(value))
        }

        @JvmStatic
        fun fromCode(code: Int): Gender {
            return entries.find {
                it.code == code
            } ?: throw GenderValidationException(UNKNOWN_GENDER_CODE_MESSAGE.format(code))
        }
    }

    @JsonValue
    fun toValue(): String {
        return this.name
    }
}