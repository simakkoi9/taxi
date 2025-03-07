package io.simakkoi9.driverservice.model.mapper

import io.simakkoi9.driverservice.model.dto.kafka.KafkaCarDto
import io.simakkoi9.driverservice.model.dto.kafka.KafkaDriverResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.Driver
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface KafkaDriverMapper {

    fun toDto(driver: Driver): KafkaDriverResponse

    fun toDto(car: Car): KafkaCarDto

}