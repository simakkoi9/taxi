package io.simakkoi9.driverservice.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Gender(val code: Int) {
    MALE(1),
    FEMALE(2);

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): Gender {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException("Unknown gender status: $value")
        }

        @JvmStatic
        fun fromCode(code: Int): Gender {
            return entries.find {
                it.code == code
            } ?: throw IllegalArgumentException("Unknown gender type code: $code")
        }
    }

    @JsonValue
    fun toValue(): String {
        return this.name
    }
}