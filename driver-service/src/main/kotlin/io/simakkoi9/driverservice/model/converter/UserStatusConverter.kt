package io.simakkoi9.driverservice.model.converter

import io.simakkoi9.driverservice.model.entity.UserStatus
import jakarta.persistence.Converter
import jakarta.persistence.AttributeConverter

@Converter(autoApply = true)
class UserStatusConverter : AttributeConverter<UserStatus, Int> {
    override fun convertToDatabaseColumn(userStatus: UserStatus?): Int? {
        return userStatus?.code
    }

    override fun convertToEntityAttribute(integer: Int?): UserStatus? {
        return integer?.let { UserStatus.fromCode(it) }
    }
}