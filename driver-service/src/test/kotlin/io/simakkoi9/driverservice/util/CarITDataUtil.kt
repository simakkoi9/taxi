package io.simakkoi9.driverservice.util

import io.simakkoi9.driverservice.model.dto.rest.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.response.CarResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.EntryStatus
import java.time.LocalDateTime

object CarITDataUtil {

    const val INVALID_ID = 999L

    const val API_BASE_PATH = "/api/v1"
    const val CARS_ENDPOINT = "/cars"

    const val INVALID_JSON = "{ \"brand\": \"Toyota\", \"model\": \"Camry\", invalid json }"

    fun getCarCreateRequest(
        brand: String = "Toyota",
        model: String = "Camry",
        color: String = "White",
        number: String = "BY1563"
    ): CarCreateRequest = CarCreateRequest(brand, model, color, number)

    fun getCarUpdateRequest(
        brand: String? = null,
        model: String? = "Corolla",
        color: String? = null,
        number: String? = null
    ): CarUpdateRequest = CarUpdateRequest(brand, model, color, number)

    fun getCarResponse(
        id: Long = CarTestDataUtil.ID,
        brand: String = "Toyota",
        model: String = "Camry",
        color: String = "White",
        number: String = "BY1563",
        createdAt: LocalDateTime = LocalDateTime.now()
    ): CarResponse = CarResponse(id, brand, model, color, number, createdAt)

    fun getCar(
        id: Long? = null,
        brand: String = "Toyota",
        model: String = "Camry",
        color: String = "White",
        number: String = "BY1563",
        status: EntryStatus = EntryStatus.ACTIVE,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): Car {
        val car = Car()
        if (id != null) {
            car.id = id
        }
        car.brand = brand
        car.model = model
        car.color = color
        car.number = number
        car.status = status
        car.createdAt = createdAt
        return car
    }
}