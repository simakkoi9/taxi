package io.simakkoi9.driverservice.service

import io.simakkoi9.driverservice.model.dto.driver.request.DriverCreateRequest
import io.simakkoi9.driverservice.model.dto.driver.request.DriverUpdateRequest
import io.simakkoi9.driverservice.model.dto.driver.response.DriverResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface DriverService {
    fun createDriver(driverCreateRequest: DriverCreateRequest): DriverResponse

    fun updateDriver(id: Long, driverUpdateRequest: DriverUpdateRequest): DriverResponse

    fun setCarForDriver(driverId: Long, carId: Long): DriverResponse

    fun removeCarForDriver(id: Long): DriverResponse

    fun deleteDriver(id: Long): DriverResponse

    fun getDriver(id: Long): DriverResponse

    fun getAllDrivers(pageable: Pageable): Page<DriverResponse>
}