package io.simakkoi9.driverservice.model.converter

import io.simakkoi9.driverservice.model.entity.EntryStatus
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class EntryStatusConverter : AttributeConverter<EntryStatus, Int> {
    override fun convertToDatabaseColumn(entryStatus: EntryStatus?): Int? =
        entryStatus?.code

    override fun convertToEntityAttribute(integer: Int?): EntryStatus? =
        integer?.let { EntryStatus.fromCode(it) }
}