package io.simakkoi9.driverservice.controller

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.simakkoi9.driverservice.config.PostgresTestContainer
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.repository.CarRepository
import io.simakkoi9.driverservice.util.CarITDataUtil
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresTestContainer::class)
class CarControllerIT {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var carRepository: CarRepository

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", PostgresTestContainer.getInstance()::getJdbcUrl)
            registry.add("spring.datasource.username", PostgresTestContainer.getInstance()::getUsername)
            registry.add("spring.datasource.password", PostgresTestContainer.getInstance()::getPassword)
        }
    }

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        RestAssured.basePath = CarITDataUtil.API_BASE_PATH
    }

    @AfterEach
    fun cleanUp() {
        carRepository.deleteAll()
    }

    @Test
    fun testCreateCar_Valid() {
        val request = CarITDataUtil.getCarCreateRequest()

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
                .post(CarITDataUtil.CARS_ENDPOINT)
            .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path<Int>("id")

        val createdCar = carRepository.findById(response.toLong()).orElse(null)
        assertAll(
            { assertNotNull(createdCar) },
            { assertEquals(request.brand, createdCar.brand) },
            { assertEquals(request.model, createdCar.model) },
            { assertEquals(request.color, createdCar.color) },
            { assertEquals(request.number, createdCar.number) }
        )
    }

    @Test
    fun testUpdateCar_Valid() {
        val car = CarITDataUtil.getCar()
        val savedCar = carRepository.save(car)
        val updateRequest = CarITDataUtil.getCarUpdateRequest()

        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
            .`when`()
                .patch("${CarITDataUtil.CARS_ENDPOINT}/${savedCar.id!!}")
            .then()
                .statusCode(HttpStatus.OK.value())

        val updatedCar = carRepository.findById(savedCar.id!!).orElse(null)
        assertAll(
            { assertNotNull(updatedCar) },
            { assertEquals(savedCar.brand, updatedCar.brand) },
            { assertEquals(updateRequest.model, updatedCar.model) },
            { assertEquals(savedCar.color, updatedCar.color) },
            { assertEquals(savedCar.number, updatedCar.number) }
        )
    }

    @Test
    fun testDeleteCar_Valid() {
        val car = CarITDataUtil.getCar()
        val savedCar = carRepository.save(car)

        given()
            .`when`()
            .delete("${CarITDataUtil.CARS_ENDPOINT}/${savedCar.id!!}")
            .then()
            .statusCode(HttpStatus.OK.value())

        val deletedCar = carRepository.findById(savedCar.id!!).orElse(null)
        assertEquals(EntryStatus.DELETED, deletedCar.status)
    }

    @Test
    fun testGetCar_Valid() {
        val car = CarITDataUtil.getCar()
        val savedCar = carRepository.save(car)

        given()
            .`when`()
                .get("${CarITDataUtil.CARS_ENDPOINT}/${savedCar.id!!}")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(savedCar.id!!.toInt()))
                .body("brand", equalTo(savedCar.brand))
                .body("model", equalTo(savedCar.model))
                .body("color", equalTo(savedCar.color))
                .body("number", equalTo(savedCar.number))
    }

    @Test
    fun testGetAllDrivers_Valid() {
        val car1 = CarITDataUtil.getCar()
        val car2 = CarITDataUtil.getCar(model = "Corolla")
        carRepository.saveAll(listOf(car1, car2))

        given()
            .`when`()
                .get(CarITDataUtil.CARS_ENDPOINT)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(2))
                .body("totalElements", equalTo(2))
    }

    @Test
    fun testGetCar_NotFound() {
        given()
            .`when`()
                .get("${CarITDataUtil.CARS_ENDPOINT}/${CarITDataUtil.INVALID_ID}")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testUpdateCar_NotFound() {
        val updateRequest = CarITDataUtil.getCarUpdateRequest()

        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
            .`when`()
                .patch("${CarITDataUtil.CARS_ENDPOINT}/${CarITDataUtil.INVALID_ID}")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testDeleteCar_NotFound() {
        given()
            .`when`()
                .delete("${CarITDataUtil.CARS_ENDPOINT}/${CarITDataUtil.INVALID_ID}")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testCreateCar_Duplicate() {
        val car = CarITDataUtil.getCar()
        carRepository.save(car)
        
        val request = CarITDataUtil.getCarCreateRequest(number = car.number!!)

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .`when`()
                .post(CarITDataUtil.CARS_ENDPOINT)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors", notNullValue())
    }

    @Test
    fun testCreateCar_InvalidJson() {
        val invalidJson = CarITDataUtil.INVALID_JSON

        given()
            .contentType(ContentType.JSON)
            .body(invalidJson)
            .`when`()
                .post(CarITDataUtil.CARS_ENDPOINT)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors", notNullValue())
    }
}