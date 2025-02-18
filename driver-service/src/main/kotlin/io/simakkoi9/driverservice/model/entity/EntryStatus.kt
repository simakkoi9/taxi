package io.simakkoi9.driverservice.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.simakkoi9.driverservice.util.ErrorMessages.UNKNOWN_STATUS_CODE
import io.simakkoi9.driverservice.util.ErrorMessages.UNKNOWN_STATUS_VALUE

enum class EntryStatus(val code: Int) {
    ACTIVE(1),
    DELETED(2);

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): EntryStatus {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException(UNKNOWN_STATUS_VALUE.format(value))
        }

        @JvmStatic
        fun fromCode(code: Int): EntryStatus {
            return entries.find {
                it.code == code
            } ?: throw IllegalArgumentException(UNKNOWN_STATUS_CODE.format(code))
        }
    }

    @JsonValue
    fun toValue(): String {
        return this.name
    }
}
