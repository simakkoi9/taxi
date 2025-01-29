package io.simakkoi9.driverservice.controller

import io.simakkoi9.driverservice.model.dto.driver.request.DriverCreateRequest
import io.simakkoi9.driverservice.model.dto.driver.request.DriverUpdateRequest
import io.simakkoi9.driverservice.model.dto.driver.response.DriverResponse
import io.simakkoi9.driverservice.service.DriverService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/drivers")
class DriverController(
    private val driverService: DriverService
) {
    @PostMapping
    fun createDriver(@Validated @RequestBody driverCreateRequest: DriverCreateRequest) : DriverResponse {
        return driverService.createDriver(driverCreateRequest)
    }

    @PutMapping("/{id}")
    fun updateDriver(
        @PathVariable id: Long,
        @Validated @RequestBody driverUpdateRequest: DriverUpdateRequest
    ) : DriverResponse {
        return driverService.updateDriver(id, driverUpdateRequest)
    }

    @PatchMapping("/{driverId}/setCar/{carId}")
    fun updateCarForDriver(
        @PathVariable driverId: Long,
        @PathVariable carId: Long
    ) : DriverResponse {
        return driverService.setCarForDriver(driverId, carId)
    }

    @PatchMapping("/{id}/removeCar")
    fun removeCarForDriver(@PathVariable id: Long) : DriverResponse {
        return driverService.removeCarForDriver(id)
    }

    @DeleteMapping("/{id}")
    fun deleteDriver(@PathVariable id: Long) : DriverResponse {
        return driverService.deleteDriver(id)
    }

    @GetMapping("/{id}")
    fun getDriver(@PathVariable id: Long) : DriverResponse {
        return driverService.getDriver(id)
    }

    @GetMapping
    fun getAllDrivers() : List<DriverResponse> {
        return driverService.getAllDrivers()
    }
}