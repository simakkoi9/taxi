package io.simakkoi9.driverservice.e2e.steps

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.repository.CarRepository
import io.simakkoi9.driverservice.util.CarTestDataUtil
import io.simakkoi9.driverservice.util.E2eConstants
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus

class CarSteps {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private lateinit var carRepository: CarRepository

    private lateinit var carRequestBody: String
    private var carId: Long = 0
    private lateinit var response: Response

    private fun requestSpec(): RequestSpecification {
        return RestAssured.given()
            .baseUri(E2eConstants.BASE_URL)
            .port(port)
            .contentType(ContentType.JSON)
    }

    @Given("car request body:")
    fun carRequestBody(requestBody: String) {
        this.carRequestBody = requestBody
    }

    @Given("car id: {long}")
    fun carId(id: Long) {
        this.carId = id
    }

    @Given("car id from response")
    fun carIdFromResponse() {
        this.carId = response.jsonPath().getLong("id")
    }

    @When("sending request to create new car")
    fun sendCreateCarRequest() {
        response = requestSpec()
            .body(carRequestBody)
            .post(E2eConstants.CARS_URL)
    }

    @When("sending request to update car")
    fun sendUpdateCarRequest() {
        response = requestSpec()
            .body(carRequestBody)
            .patch("${E2eConstants.CARS_URL}/$carId")
    }

    @When("sending request to get car by id")
    fun sendGetCarRequest() {
        response = requestSpec()
            .get("${E2eConstants.CARS_URL}/$carId")
    }

    @When("sending request to delete car")
    fun sendDeleteCarRequest() {
        response = requestSpec()
            .delete("${E2eConstants.CARS_URL}/$carId")
    }

    @When("sending request to get all cars")
    fun sendGetAllCarsRequest() {
        response = requestSpec()
            .get(E2eConstants.CARS_URL)
    }

    @Then("should get a car response with status {int}")
    fun verifyCarResponse(statusCode: Int) {
        response
            .then()
            .statusCode(statusCode)
            .body("id", notNullValue())

        if (statusCode == HttpStatus.OK.value()) {
            val id = response.jsonPath().getLong("id")
            assertTrue(carRepository.existsById(id))
        }
    }

    @Then("should get a updated car with status {int}")
    fun verifyUpdatedCarResponse(statusCode: Int) {
        response
            .then()
            .statusCode(statusCode)
            .body("id", notNullValue())

        if (statusCode == HttpStatus.OK.value()) {
            val car = carRepository.findById(carId)
            assertTrue(car.isPresent)
            assertEquals(response.jsonPath().getString("model"), car.get().model)
        }
    }

    @Then("should get a car error response with status {int}")
    fun verifyErrorResponse(statusCode: Int) {
        assertEquals(statusCode, response.statusCode)
    }

    @Then("should get a list of cars with status {int}")
    fun verifyCarListResponse(statusCode: Int) {
        response
            .then()
            .statusCode(statusCode)
            .body("content", notNullValue())
            .body("size", notNullValue())
            .body("page", notNullValue())
            .body("totalPages", notNullValue())
            .body("totalElements", notNullValue())

        if (statusCode == HttpStatus.OK.value()) {
            val totalElements = response.jsonPath().getLong("totalElements")
            assertEquals(
                totalElements,
                carRepository.findAllByStatus(
                    EntryStatus.ACTIVE,
                    PageRequest.of(CarTestDataUtil.PAGE, CarTestDataUtil.SIZE)
                ).totalElements
            )
        }
    }
}