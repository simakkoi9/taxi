package io.simakkoi9.driverservice.service

import io.simakkoi9.driverservice.model.dto.kafka.KafkaDriverDto
import io.simakkoi9.driverservice.model.dto.rest.PageResponse
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.response.DriverResponse

interface DriverService {
    fun createDriver(driverCreateRequest: DriverCreateRequest): DriverResponse

    fun updateDriver(id: Long, driverUpdateRequest: DriverUpdateRequest): DriverResponse

    fun setCarForDriver(driverId: Long, carId: Long): DriverResponse

    fun removeCarForDriver(id: Long): DriverResponse

    fun deleteDriver(id: Long): DriverResponse

    fun getDriver(id: Long): DriverResponse

    fun getAvailableDriverForRide(driverIdList: List<Long>): KafkaDriverDto

    fun getAllDrivers(page: Int, size: Int): PageResponse<DriverResponse>
}