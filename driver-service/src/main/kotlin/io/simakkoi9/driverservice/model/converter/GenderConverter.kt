package io.simakkoi9.driverservice.model.converter

import io.simakkoi9.driverservice.model.entity.Gender
import jakarta.persistence.Converter
import jakarta.persistence.AttributeConverter

@Converter(autoApply = true)
class GenderConverter : AttributeConverter<Gender, Int> {
    override fun convertToDatabaseColumn(gender: Gender?): Int? {
        return gender?.code
    }

    override fun convertToEntityAttribute(integer: Int?): Gender? {
        return integer?.let { Gender.fromCode(it) }
    }
}