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

    fun getDriver(
        id: Long? = null,
        name: String = "name",
        email: String = "email@mail.com",
        phone: String = "+375291234567",
        gender: Gender = Gender.MALE,
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