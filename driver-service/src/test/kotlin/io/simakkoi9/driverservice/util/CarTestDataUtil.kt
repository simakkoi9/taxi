package io.simakkoi9.driverservice.util

import io.simakkoi9.driverservice.model.dto.rest.PageResponse
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.response.CarResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.EntryStatus
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

object CarTestDataUtil {

    const val ID = 1L
    const val PAGE = 0
    const val SIZE = 10
    const val TOTAL_PAGES = 1
    const val TOTAL_ELEMENTS = 1L

    const val BRAND = "Toyota"
    const val MODEL = "Camry"
    const val ANOTHER_MODEL = "Corolla"
    const val COLOR = "White"
    const val NUMBER = "BY1563"

    fun getDuplicateCarErrorMessage(number: String): String {
        return "Car with number $number already exists."
    }

    fun getCarNotFoundErrorMessage(id: Long): String {
        return "Car $id not found."
    }

    fun getCarCreateRequest(
        brand: String = BRAND,
        model: String = MODEL,
        color: String = COLOR,
        number: String = NUMBER
    ): CarCreateRequest = CarCreateRequest(brand, model, color, number)

    fun getCarUpdateRequest(
        brand: String? = null,
        model: String? = ANOTHER_MODEL,
        color: String? = null,
        number: String? = null
    ): CarUpdateRequest = CarUpdateRequest(brand, model, color, number)

    fun getCarResponse(
        id: Long = ID,
        brand: String = BRAND,
        model: String = MODEL,
        color: String = COLOR,
        number: String = NUMBER,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): CarResponse = CarResponse(id, brand, model, color, number, createdAt)

    fun getCar(
        id: Long = ID,
        brand: String = BRAND,
        model: String = MODEL,
        color: String = COLOR,
        number: String = NUMBER,
        status: EntryStatus = EntryStatus.ACTIVE,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): Car {
        val car = Car()
        car.id = id
        car.brand = brand
        car.model = model
        car.color = color
        car.number = number
        car.status = status
        car.createdAt = createdAt
        return car
    }

    fun getPageRequest(): PageRequest = PageRequest.of(PAGE, SIZE)

    fun <T> getPageResponse(list: List<T>): PageResponse<T> =
        PageResponse(
            list,
            SIZE,
            PAGE,
            TOTAL_PAGES,
            TOTAL_ELEMENTS
        )
}