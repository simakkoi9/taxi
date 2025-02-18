package io.simakkoi9.driverservice.service

import io.simakkoi9.driverservice.model.dto.PageResponse
import io.simakkoi9.driverservice.model.dto.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.car.response.CarResponse

interface CarService {
    fun createCar(carCreateRequest: CarCreateRequest): CarResponse

    fun updateCar(id: Long, carUpdateRequest: CarUpdateRequest): CarResponse

    fun deleteCar(id: Long): CarResponse

    fun getCar(id: Long): CarResponse

    fun getAllCars(page: Int, size: Int): PageResponse<CarResponse>
}