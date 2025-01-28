package io.simakkoi9.driverservice.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class UserStatus(val code: Int) {
    ACTIVE(1),
    DELETED(2);

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): UserStatus {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException("Unknown status: $value")
        }

        @JvmStatic
        fun fromCode(code: Int): UserStatus {
            return entries.find {
                it.code == code
            } ?: throw IllegalArgumentException("Unknown code: $code")
        }
    }

    @JsonValue
    fun toValue(): String {
        return this.name
    }
}
