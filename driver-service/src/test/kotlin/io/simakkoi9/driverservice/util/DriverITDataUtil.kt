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

    const val EXTERNAL_ID = "default"
    const val NAME = "name"
    const val ANOTHER_NAME = "otherName"
    const val EMAIL = "email@mail.com"
    const val INVALID_EMAIL = "invalid-email"
    const val PHONE = "+375442345678"
    const val INVALID_PHONE = "123"
    val GENDER = Gender.MALE

    const val HEADER_ID = "X-User-Id"
    const val HEADER_ROLE = "X-User-Role"
    const val ROLE_ADMIN = "ROLE_ADMIN"

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

    fun getDriver(
        id: Long? = null,
        externalId: String = EXTERNAL_ID,
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
        driver.externalId = externalId
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