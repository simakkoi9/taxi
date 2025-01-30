package io.simakkoi9.driverservice.controller

import io.simakkoi9.driverservice.model.dto.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.car.response.CarResponse
import io.simakkoi9.driverservice.service.CarService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/cars")
class CarController(
    private val carService: CarService
) {
    @PostMapping
    fun createCar(@Validated @RequestBody carCreateRequest: CarCreateRequest): CarResponse {
        return carService.createCar(carCreateRequest)
    }

    @PutMapping("/{id}")
    fun updateCar(
        @PathVariable id: Long,
        @Validated @RequestBody carUpdateRequest: CarUpdateRequest
    ): CarResponse {
        return carService.updateCar(id, carUpdateRequest)
    }

    @DeleteMapping("/{id}")
    fun deleteCar(@PathVariable id: Long): CarResponse {
        return carService.deleteCar(id)
    }

    @GetMapping("/{id}")
    fun getCar(@PathVariable id: Long): CarResponse {
        return carService.getCar(id)
    }

    @GetMapping
    fun getAllCars(@PageableDefault(page = 0, size = 10) pageable: Pageable): Page<CarResponse> {
        return carService.getAllCars(pageable)
    }
}