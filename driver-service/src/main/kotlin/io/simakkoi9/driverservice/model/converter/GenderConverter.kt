package io.simakkoi9.driverservice.model.converter

import io.simakkoi9.driverservice.model.entity.Gender
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class GenderConverter : AttributeConverter<Gender, Int> {
    override fun convertToDatabaseColumn(gender: Gender?): Int? =
        gender?.code

    override fun convertToEntityAttribute(integer: Int?): Gender? =
        integer?.let { Gender.fromCode(it) }
}