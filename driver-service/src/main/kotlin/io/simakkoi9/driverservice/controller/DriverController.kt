package io.simakkoi9.driverservice.controller

import io.simakkoi9.driverservice.model.dto.rest.PageResponse
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.response.DriverResponse
import io.simakkoi9.driverservice.security.AccessType
import io.simakkoi9.driverservice.security.RequiredUserAccess
import io.simakkoi9.driverservice.service.DriverService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/drivers")
@Validated
class DriverController(
    private val driverService: DriverService
) {

    @PostMapping
    @RequiredUserAccess(accessType = AccessType.SERVICE_OR_ADMIN_ONLY)
    fun createDriver(@Valid @RequestBody driverCreateRequest: DriverCreateRequest): DriverResponse =
        driverService.createDriver(driverCreateRequest)

    @PatchMapping("/{id}")
    @RequiredUserAccess(accessType = AccessType.USER)
    fun updateDriver(
        @PathVariable id: Long,
        @Valid @RequestBody driverUpdateRequest: DriverUpdateRequest
    ): DriverResponse = driverService.updateDriver(id, driverUpdateRequest)

    @PatchMapping("/{driverId}/setCar")
    @RequiredUserAccess(accessType = AccessType.USER)
    fun updateCarForDriver(
        @PathVariable driverId: Long,
        @RequestParam(name = "id") carId: Long
    ): DriverResponse =
        driverService.setCarForDriver(driverId, carId)

    @PatchMapping("/{id}/removeCar")
    @RequiredUserAccess(accessType = AccessType.USER)
    fun removeCarForDriver(@PathVariable id: Long): DriverResponse =
        driverService.removeCarForDriver(id)

    @DeleteMapping("/{id}")
    @RequiredUserAccess(accessType = AccessType.USER)
    fun deleteDriver(@PathVariable id: Long): DriverResponse =
        driverService.deleteDriver(id)

    @GetMapping("/{id}")
    @RequiredUserAccess(accessType = AccessType.USER)
    fun getDriver(@PathVariable id: Long): DriverResponse =
        driverService.getDriver(id)

    @GetMapping
    @RequiredUserAccess(accessType = AccessType.ADMIN_ONLY)
    fun getAllDrivers(
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "10") @Min(1) size: Int
    ): PageResponse<DriverResponse> = driverService.getAllDrivers(page, size)
}