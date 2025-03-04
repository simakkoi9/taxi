package io.simakkoi9.driverservice.model.mapper

import io.simakkoi9.driverservice.model.dto.rest.PageResponse
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.response.CarResponse
import io.simakkoi9.driverservice.model.entity.Car
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy
import org.springframework.data.domain.Page


@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING
)
interface CarMapper {
    fun toEntity(carCreateRequest: CarCreateRequest): Car

    fun toResponse(car: Car): CarResponse

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun partialUpdate(carUpdateRequest: CarUpdateRequest, @MappingTarget car: Car): Car

    fun toResponseList(cars: List<Car>): List<CarResponse>

    fun toPageResponse(cars: Page<Car>): PageResponse<CarResponse> {
        val carResponseList: List<CarResponse> = toResponseList(cars.content)
        return PageResponse(
            carResponseList,
            cars.size,
            cars.number,
            cars.totalPages,
            cars.totalElements
        )
    }
}