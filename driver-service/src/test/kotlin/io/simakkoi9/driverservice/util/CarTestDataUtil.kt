package io.simakkoi9.driverservice.util

import io.simakkoi9.driverservice.model.dto.rest.PageResponse
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.response.CarResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.Driver
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.model.entity.Gender
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

object CarTestDataUtil {

    const val ID = 1L
    const val PAGE = 0
    const val SIZE = 10
    const val TOTAL_PAGES = 1
    const val TOTAL_ELEMENTS = 1L

    fun getDuplicateCarErrorMessage(number: String): String {
        return "Car with number $number already exists."
    }

    fun getCarNotFoundErrorMessage(id: Long): String {
        return "Car $id not found."
    }

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
        id: Long = ID,
        brand: String = "Toyota",
        model: String = "Camry",
        color: String = "White",
        number: String = "BY1563",
        createdAt: LocalDateTime = LocalDateTime.now()
    ): CarResponse = CarResponse(id, brand, model, color, number, createdAt)

    fun getCar(
        id: Long = ID,
        brand: String = "Toyota",
        model: String = "Camry",
        color: String = "White",
        number: String = "BY1563",
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

    fun getDriver(
        id: Long = ID,
        name: String = "name",
        email: String = "email@mail.com",
        phone: String = "+375291234567",
        gender: Gender = Gender.MALE,
        car: Car = getCar(),
        status: EntryStatus = EntryStatus.ACTIVE,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): Driver {
        val driver = Driver()
        driver.id = id
        driver.name = name
        driver.email = email
        driver.phone = phone
        driver.car = car
        driver.status = status
        driver.gender = gender
        driver.createdAt = createdAt
        return driver
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