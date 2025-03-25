package io.simakkoi9.driverservice.util

import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverUpdateRequest
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.Driver
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.model.entity.Gender
import java.time.LocalDateTime

object DriverITDataUtil {

    const val INVALID_ID = 999L

    const val API_BASE_PATH = "/api/v1"
    const val DRIVERS_ENDPOINT = "/drivers"

    const val INVALID_JSON = "{ \"name\": \"John Doe\", \"email\": \"johndoe@mail.com\", invalid json }"

    const val NAME = "name"
    const val ANOTHER_NAME = "otherName"
    const val EMAIL = "email@mail.com"
    const val INVALID_EMAIL = "invalid-email"
    const val PHONE = "+375442345678"
    const val INVALID_PHONE = "123"
    val GENDER = Gender.MALE

    fun getDriverCreateRequest(
        name: String = NAME,
        email: String = EMAIL,
        phone: String = PHONE,
        gender: Gender = GENDER
    ):DriverCreateRequest = DriverCreateRequest(name, email, phone, gender)

    fun getInvalidDriverCreateRequest(
        name: String = "",
        email: String = INVALID_EMAIL,
        phone: String = INVALID_PHONE,
        gender: Gender = GENDER
    ): DriverCreateRequest = DriverCreateRequest(name, email, phone, gender)

    fun getDriverUpdateRequest(
        name: String? = ANOTHER_NAME,
        email: String? = null,
        phone: String? = null,
        gender: Gender? = null
    ):DriverUpdateRequest = DriverUpdateRequest(name, email, phone, gender)

    fun getDriver(
        id: Long? = null,
        name: String = NAME,
        email: String = EMAIL,
        phone: String = PHONE,
        gender: Gender = GENDER,
        car: Car? = null,
        status: EntryStatus = EntryStatus.ACTIVE,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): Driver {
        val driver = Driver()
        if (id != null) {
            driver.id = id
        }
        driver.name = name
        driver.email = email
        driver.phone = phone
        if (car != null) {
            driver.car = car
        }
        driver.status = status
        driver.gender = gender
        driver.createdAt = createdAt
        return driver
    }
} 