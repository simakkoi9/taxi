package io.simakkoi9.driverservice.model.mapper

import io.simakkoi9.driverservice.model.dto.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.car.response.CarResponse
import io.simakkoi9.driverservice.model.entity.Car
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
interface CarMapper {
    fun toEntity(carCreateRequest: CarCreateRequest): Car

    fun toResponse(car: Car): CarResponse

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun partialUpdate(carUpdateRequest: CarUpdateRequest, @MappingTarget car: Car): Car
}