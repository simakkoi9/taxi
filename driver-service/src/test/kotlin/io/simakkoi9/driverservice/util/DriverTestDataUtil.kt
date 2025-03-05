package io.simakkoi9.driverservice.util

import io.simakkoi9.driverservice.model.dto.kafka.KafkaCarDto
import io.simakkoi9.driverservice.model.dto.kafka.KafkaDriverResponse
import io.simakkoi9.driverservice.model.dto.rest.PageResponse
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.response.DriverResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.Driver
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.model.entity.Gender
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime


object DriverTestDataUtil {
    const val ID = 1L
    const val PAGE = 0
    const val SIZE = 10
    const val TOTAL_PAGES = 1
    const val TOTAL_ELEMENTS = 1L

    const val API_BASE_PATH = "/api/v1"
    const val DRIVERS_ENDPOINT = "/drivers"

    fun getDuplicateDriverErrorMessage(email: String): String {
        return "Driver with email $email already exists."
    }

    fun getCarIsNotAvailableErrorMessage(): String {
        return "Car is already using."
    }

    fun getDriverNotFoundErrorMessage(id: Long): String {
        return "Driver $id not found."
    }

    fun getDriverCreateRequest(
        name: String = "name",
        email: String = "email@mail.com",
        phone: String = "+375442345678",
        gender: Gender = Gender.MALE
    ):DriverCreateRequest = DriverCreateRequest(name, email, phone, gender)

    fun getInvalidDriverCreateRequest(
        name: String = "",
        email: String = "invalid-email",
        phone: String = "123",
        gender: Gender = Gender.MALE
    ): DriverCreateRequest = DriverCreateRequest(name, email, phone, gender)

    fun getDriverUpdateRequest(
        name: String? = "otherName",
        email: String? = null,
        phone: String? = null,
        gender: Gender? = null
    ):DriverUpdateRequest = DriverUpdateRequest(name, email, phone, gender)

    fun getDriverResponse(
        id: Long = ID,
        name: String = "name",
        email: String = "email@mail.com",
        phone: String = "+375442345678",
        gender: Gender = Gender.MALE,
        carId: Long? = null,
        createdAt: LocalDateTime = LocalDateTime.now()
    ):DriverResponse = DriverResponse(id, name, email, phone, gender, carId, createdAt)

    fun getKafkaDriverResponse(
        id: Long = ID,
        name: String = "name",
        email: String = "email@mail.com",
        phone: String = "+375442345678",
        gender: Gender = Gender.MALE,
        car: KafkaCarDto = getKafkaCarDto()
    ): KafkaDriverResponse = KafkaDriverResponse(id, name, email, phone, gender, car)

    private fun getKafkaCarDto(
        id: Long = CarTestDataUtil.ID,
        brand: String = "Toyota",
        model: String = "Camry",
        color: String = "White",
        number: String = "BY1563"
    ): KafkaCarDto = KafkaCarDto(id, brand, model, color, number)

    fun getDriver(
        id: Long = ID,
        name: String = "name",
        email: String = "email@mail.com",
        phone: String = "+375291234567",
        gender: Gender = Gender.MALE,
        car: Car = CarTestDataUtil.getCar(),
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

    fun getPageRequest(): PageRequest = PageRequest.of(CarTestDataUtil.PAGE, CarTestDataUtil.SIZE)

    fun <T> getPageResponse(list: List<T>): PageResponse<T> =
        PageResponse(
            list,
            CarTestDataUtil.SIZE,
            CarTestDataUtil.PAGE,
            CarTestDataUtil.TOTAL_PAGES,
            CarTestDataUtil.TOTAL_ELEMENTS
        )
}