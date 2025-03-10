package io.simakkoi9.driverservice.e2e.steps

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import io.simakkoi9.driverservice.util.E2eConstants
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.web.server.LocalServerPort

class DriverSteps {

    @LocalServerPort
    private val port: Int = 0

    private lateinit var driverRequestBody: String
    private var driverId: Long = 0
    private var carId: Long = 0
    private lateinit var response: Response

    private fun requestSpec(): RequestSpecification {
        return RestAssured.given()
            .baseUri(E2eConstants.BASE_URL)
            .port(port)
            .contentType(ContentType.JSON)
    }

    @Given("driver request body:")
    fun driverRequestBody(requestBody: String) {
        this.driverRequestBody = requestBody
    }

    @Given("driver id: {long}")
    fun driverId(id: Long) {
        this.driverId = id
    }

    @Given("driver id from response")
    fun driverIdFromResponse() {
        this.driverId = response.jsonPath().getLong("id")
    }
    
    @Given("car id for driver: {long}")
    fun carId(id: Long) {
        this.carId = id
    }

    @When("sending request to create new driver")
    fun sendCreateDriverRequest() {
        response = requestSpec()
            .body(driverRequestBody)
            .post(E2eConstants.DRIVERS_URL)
    }

    @When("sending request to update driver")
    fun sendUpdateDriverRequest() {
        response = requestSpec()
            .body(driverRequestBody)
            .patch("${E2eConstants.DRIVERS_URL}/$driverId")
    }

    @When("sending request to set car for driver")
    fun sendSetCarForDriverRequest() {
        response = requestSpec()
            .param("id", carId)
            .patch("${E2eConstants.DRIVERS_URL}/$driverId/setCar")
    }

    @When("sending request to remove car from driver")
    fun sendRemoveCarFromDriverRequest() {
        response = requestSpec()
            .patch("${E2eConstants.DRIVERS_URL}/$driverId/removeCar")
    }

    @When("sending request to get driver by id")
    fun sendGetDriverRequest() {
        response = requestSpec()
            .get("${E2eConstants.DRIVERS_URL}/$driverId")
    }

    @When("sending request to delete driver")
    fun sendDeleteDriverRequest() {
        response = requestSpec()
            .delete("${E2eConstants.DRIVERS_URL}/$driverId")
    }

    @When("sending request to get all drivers")
    fun sendGetAllDriversRequest() {
        response = requestSpec()
            .get(E2eConstants.DRIVERS_URL)
    }

    @Then("should get a driver response with status {int}")
    fun verifyDriverResponse(statusCode: Int) {
        response
            .then()
            .statusCode(statusCode)
            .body("id", notNullValue())
    }

    @Then("should get a updated driver with status {int}")
    fun verifyUpdatedDriverResponse(statusCode: Int) {
        response
            .then()
            .statusCode(statusCode)
            .body("id", notNullValue())
            .body("name", equalTo("otherName"))
    }

    @Then("should get a driver with car response with status {int}")
    fun verifyDriverWithCarResponse(statusCode: Int) {
        response
            .then()
            .statusCode(statusCode)
            .body("id", notNullValue())
            .body("carId", notNullValue())
    }

    @Then("should get a driver error response with status {int}")
    fun verifyErrorResponse(statusCode: Int) {
        assertEquals(statusCode, response.statusCode)
    }

    @Then("should get a list of drivers with status {int}")
    fun verifyDriverListResponse(statusCode: Int) {
        response
            .then()
            .statusCode(statusCode)
            .body("content", notNullValue())
            .body("size", notNullValue())
            .body("page", notNullValue())
            .body("totalPages", notNullValue())
            .body("totalElements", notNullValue())
    }
}