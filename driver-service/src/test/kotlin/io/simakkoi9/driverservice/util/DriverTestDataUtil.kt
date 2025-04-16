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

    const val EXTERNAL_ID = "default"
    const val NAME = "name"
    const val ANOTHER_NAME = "otherName"
    const val EMAIL = "email@mail.com"
    const val INVALID_EMAIL = "invalid-email"
    const val PHONE = "+375442345678"
    const val INVALID_PHONE = "123"
    val GENDER = Gender.MALE

    const val BRAND = "Toyota"
    const val MODEL = "Camry"
    const val COLOR = "White"
    const val NUMBER = "BY1563"

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
        externalId: String = EXTERNAL_ID,
        name: String = NAME,
        email: String = EMAIL,
        phone: String = PHONE,
        gender: Gender = GENDER
    ):DriverCreateRequest = DriverCreateRequest(externalId, name, email, phone, gender)

    fun getInvalidDriverCreateRequest(
        externalId: String = EXTERNAL_ID,
        name: String = "",
        email: String = INVALID_EMAIL,
        phone: String = INVALID_PHONE,
        gender: Gender = GENDER
    ): DriverCreateRequest = DriverCreateRequest(externalId, name, email, phone, gender)

    fun getDriverUpdateRequest(
        name: String? = ANOTHER_NAME,
        email: String? = null,
        phone: String? = null,
        gender: Gender? = null
    ):DriverUpdateRequest = DriverUpdateRequest(name, email, phone, gender)

    fun getDriverResponse(
        id: Long = ID,
        name: String = NAME,
        email: String = EMAIL,
        phone: String = PHONE,
        gender: Gender = GENDER,
        carId: Long? = null,
        createdAt: LocalDateTime = LocalDateTime.now()
    ):DriverResponse = DriverResponse(id, name, email, phone, gender, carId, createdAt)

    fun getKafkaDriverResponse(
        id: Long = ID,
        name: String = NAME,
        email: String = EMAIL,
        phone: String = PHONE,
        gender: Gender = GENDER,
        car: KafkaCarDto = getKafkaCarDto()
    ): KafkaDriverResponse = KafkaDriverResponse(id, name, email, phone, gender, car)

    private fun getKafkaCarDto(
        id: Long = ID,
        brand: String = BRAND,
        model: String = MODEL,
        color: String = COLOR,
        number: String = NUMBER
    ): KafkaCarDto = KafkaCarDto(id, brand, model, color, number)

    fun getDriver(
        id: Long = ID,
        name: String = NAME,
        email: String = EMAIL,
        phone: String = PHONE,
        gender: Gender = GENDER,
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