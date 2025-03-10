package io.simakkoi9.driverservice.controller

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.simakkoi9.driverservice.config.PostgresTestContainer
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.repository.CarRepository
import io.simakkoi9.driverservice.repository.DriverRepository
import io.simakkoi9.driverservice.util.CarITDataUtil
import io.simakkoi9.driverservice.util.DriverITDataUtil
import org.hamcrest.Matchers.emptyArray
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(PostgresTestContainer::class)
class DriverControllerIT {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var driverRepository: DriverRepository

    @Autowired
    private lateinit var carRepository: CarRepository

    @BeforeEach
    fun setup() {
        RestAssured.port = port
        RestAssured.basePath = DriverITDataUtil.API_BASE_PATH
    }

    @AfterEach
    fun cleanup() {
        driverRepository.deleteAll()
        carRepository.deleteAll()
    }

    @Test
    fun testCreateDriver_Valid() {
        val request = DriverITDataUtil.getDriverCreateRequest()

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
                .post(DriverITDataUtil.DRIVERS_ENDPOINT)
            .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path<Int>("id")

        val createdDriver = driverRepository.findById(response.toLong()).orElse(null)
        assertAll(
            { assertNotNull(createdDriver) },
            { assertEquals(request.name, createdDriver.name) },
            { assertEquals(request.email, createdDriver.email) },
            { assertEquals(request.phone, createdDriver.phone) },
            { assertEquals(request.gender, createdDriver.gender) }
        )
    }

    @Test
    fun testCreateDriver_ShouldThrowValidationException() {
        val request = DriverITDataUtil.getInvalidDriverCreateRequest()

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
                .post(DriverITDataUtil.DRIVERS_ENDPOINT)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors", not(emptyArray<Any>()))
    }

    @Test
    fun testUpdateDriver_Valid() {
        val driver = DriverITDataUtil.getDriver()
        val savedDriver = driverRepository.save(driver)
        val updateRequest = DriverITDataUtil.getDriverUpdateRequest()

        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
            .`when`()
                .patch("${DriverITDataUtil.DRIVERS_ENDPOINT}/${savedDriver.id!!}")
            .then()
                .statusCode(HttpStatus.OK.value())

        val updatedDriver = driverRepository.findById(savedDriver.id!!).orElse(null)
        assertAll(
            { assertNotNull(updatedDriver) },
            { assertEquals(updateRequest.name, updatedDriver.name) },
            { assertEquals(savedDriver.email, updatedDriver.email) },
            { assertEquals(savedDriver.phone, updatedDriver.phone) }
        )
    }

    @Test
    fun testSetCarForDriver_Valid() {
        val driver = DriverITDataUtil.getDriver()
        val savedDriver = driverRepository.save(driver)
        val car = CarITDataUtil.getCar()
        val savedCar = carRepository.save(car)

        given()
            .param("id", savedCar.id!!)
            .`when`()
                .patch("${DriverITDataUtil.DRIVERS_ENDPOINT}/${savedDriver.id!!}/setCar")
            .then()
                .statusCode(HttpStatus.OK.value())

        val updatedDriver = driverRepository.findById(savedDriver.id!!).orElse(null)
        assertAll(
            { assertNotNull(updatedDriver) },
            { assertEquals(savedCar.id!!, updatedDriver.car?.id) }
        )
    }

    @Test
    fun testRemoveCarForDriver_Valid() {
        val car = CarITDataUtil.getCar()
        val savedCar = carRepository.save(car)
        val driver = DriverITDataUtil.getDriver(car = savedCar)
        val savedDriver = driverRepository.save(driver)

        given()
            .`when`()
                .patch("${DriverITDataUtil.DRIVERS_ENDPOINT}/${savedDriver.id!!}/removeCar")
            .then()
            .statusCode(HttpStatus.OK.value())

        val updatedDriver = driverRepository.findById(savedDriver.id!!).orElse(null)
        assertAll(
            { assertNotNull(updatedDriver) },
            { assertNull(updatedDriver.car) }
        )
    }

    @Test
    fun testDeleteDriver_Valid() {
        val driver = DriverITDataUtil.getDriver()
        val savedDriver = driverRepository.save(driver)

        given()
            .`when`()
            .delete("${DriverITDataUtil.DRIVERS_ENDPOINT}/${savedDriver.id!!}")
            .then()
            .statusCode(HttpStatus.OK.value())

        val deletedDriver = driverRepository.findById(savedDriver.id!!).orElse(null)
        assertEquals(EntryStatus.DELETED, deletedDriver.status)
    }

    @Test
    fun testGetDriver_Valid() {
        val driver = DriverITDataUtil.getDriver()
        val savedDriver = driverRepository.save(driver)

        given()
            .`when`()
            .get("${DriverITDataUtil.DRIVERS_ENDPOINT}/${savedDriver.id!!}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(savedDriver.id!!.toInt()))
            .body("name", equalTo(savedDriver.name))
            .body("email", equalTo(savedDriver.email))
            .body("phone", equalTo(savedDriver.phone))
            .body("gender", equalTo(savedDriver.gender!!.name))
    }

    @Test
    fun testGetAllDrivers_Valid() {
        val driver1 = DriverITDataUtil.getDriver()
        val driver2 = DriverITDataUtil.getDriver(email = "another@mail.com")
        driverRepository.saveAll(listOf(driver1, driver2))

        given()
            .`when`()
            .get(DriverITDataUtil.DRIVERS_ENDPOINT)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("content.size()", equalTo(2))
            .body("totalElements", equalTo(2))
    }

    @Test
    fun testGetDriver_NotFound() {
        given()
            .`when`()
                .get("${DriverITDataUtil.DRIVERS_ENDPOINT}/${DriverITDataUtil.INVALID_ID}")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testUpdateDriver_NotFound() {
        val updateRequest = DriverITDataUtil.getDriverUpdateRequest()

        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
            .`when`()
                .patch("${DriverITDataUtil.DRIVERS_ENDPOINT}/${DriverITDataUtil.INVALID_ID}")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testDeleteDriver_NotFound() {
        given()
            .`when`()
                .delete("${DriverITDataUtil.DRIVERS_ENDPOINT}/${DriverITDataUtil.INVALID_ID}")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testCreateDriver_Duplicate() {
        val driver = DriverITDataUtil.getDriver()
        driverRepository.save(driver)
        
        val request = DriverITDataUtil.getDriverCreateRequest(email = driver.email!!)

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
                .post(DriverITDataUtil.DRIVERS_ENDPOINT)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testSetCarForDriver_CarNotFound() {
        val driver = DriverITDataUtil.getDriver()
        val savedDriver = driverRepository.save(driver)

        given()
            .param("id", DriverITDataUtil.INVALID_ID)
            .`when`()
                .patch("${DriverITDataUtil.DRIVERS_ENDPOINT}/${savedDriver.id!!}/setCar")
            .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testSetCarForDriver_DriverNotFound() {
        val car = CarITDataUtil.getCar()
        val savedCar = carRepository.save(car)

        given()
            .param("id", savedCar.id!!)
            .`when`()
                .patch("${DriverITDataUtil.DRIVERS_ENDPOINT}/999/setCar")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testUpdateCarForDriver_CarNotAvailable() {
        val car = CarITDataUtil.getCar()
        val savedCar = carRepository.save(car)

        val driver1 = DriverITDataUtil.getDriver()
        driver1.car = savedCar
        driverRepository.save(driver1)

        val driver2 = DriverITDataUtil.getDriver(email = "another@mail.com")
        val savedDriver2 = driverRepository.save(driver2)

        given()
            .param("id", savedCar.id!!)
            .`when`()
                .patch("${DriverITDataUtil.DRIVERS_ENDPOINT}/${savedDriver2.id!!}/setCar")
            .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testRemoveCarForDriver_DriverNotFound() {
        given()
            .`when`()
                .patch("${DriverITDataUtil.DRIVERS_ENDPOINT}/999/removeCar")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testCreateDriver_InvalidJson() {
        val invalidJson = DriverITDataUtil.INVALID_JSON

        given()
            .contentType(ContentType.JSON)
            .body(invalidJson)
            .`when`()
                .post(DriverITDataUtil.DRIVERS_ENDPOINT)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors", notNullValue())
    }
} 