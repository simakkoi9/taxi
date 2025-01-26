package io.simakkoi9.driverservice.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Gender {
    MALE, FEMALE;

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): Gender {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException("Unknown status: $value")
        }
    }

    @JsonValue
    fun toValue(): String{
        return this.name
    }
}