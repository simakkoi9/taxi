package io.simakkoi9.driverservice.model.mapper

import io.simakkoi9.driverservice.model.dto.rest.PageResponse
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.response.DriverResponse
import io.simakkoi9.driverservice.model.entity.Driver
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.MappingTarget
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy
import org.springframework.data.domain.Page

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING
)
interface DriverMapper {
    @Mapping(target = "car", ignore = true)
    fun toEntity(driverCreateRequest: DriverCreateRequest): Driver

    @Mapping(source = "car.id", target = "carId")
    fun toResponse(driver: Driver): DriverResponse

    fun toResponseList(drivers: List<Driver>): List<DriverResponse>

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun partialUpdate(driverUpdateRequest: DriverUpdateRequest, @MappingTarget driver: Driver): Driver

    fun toPageResponse(drivers: Page<Driver>): PageResponse<DriverResponse> {
        val driverResponseList: List<DriverResponse> = toResponseList(drivers.content)
        return PageResponse(
            driverResponseList,
            drivers.size,
            drivers.number,
            drivers.totalPages,
            drivers.totalElements
        )
    }
}