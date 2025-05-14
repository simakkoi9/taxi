package io.simakkoi9.driverservice.e2e.steps

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.repository.DriverRepository
import io.simakkoi9.driverservice.util.DriverITDataUtil
import io.simakkoi9.driverservice.util.DriverTestDataUtil
import io.simakkoi9.driverservice.util.E2eConstants
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus

class DriverSteps {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private lateinit var driverRepository: DriverRepository

    private lateinit var driverRequestBody: String
    private var driverId: Long = 0
    private var carId: Long = 0
    private lateinit var response: Response

    private fun requestSpec(): RequestSpecification {
        return RestAssured.given()
            .baseUri(E2eConstants.BASE_URL)
            .header(DriverITDataUtil.HEADER_ID, DriverITDataUtil.EXTERNAL_ID)
            .header(DriverITDataUtil.HEADER_ROLE, DriverITDataUtil.ROLE_ADMIN)
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

        if (statusCode == HttpStatus.OK.value()) {
            val id = response.jsonPath().getLong("id")
            assertTrue(driverRepository.existsById(id))
        }
    }

    @Then("should get a updated driver with status {int}")
    fun verifyUpdatedDriverResponse(statusCode: Int) {
        response
            .then()
            .statusCode(statusCode)
            .body("id", notNullValue())
            .body("name", equalTo("otherName"))

        if (statusCode == HttpStatus.OK.value()) {
            val driver = driverRepository.findById(driverId)
            assertTrue(driver.isPresent)
            assertEquals("otherName", driver.get().name)
        }
    }

    @Then("should get a driver with car response with status {int}")
    fun verifyDriverWithCarResponse(statusCode: Int) {
        response
            .then()
            .statusCode(statusCode)
            .body("id", notNullValue())
            .body("carId", notNullValue())

        if (statusCode == HttpStatus.OK.value()) {
            val driver = driverRepository.findById(driverId)
            assertTrue(driver.isPresent)
            assertEquals(carId, driver.get().car?.id)
        }
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

        if (statusCode == HttpStatus.OK.value()) {
            val totalElements = response.jsonPath().getLong("totalElements")
            assertEquals(
                totalElements,
                driverRepository.findAllByStatus(
                    EntryStatus.ACTIVE,
                    PageRequest.of(DriverTestDataUtil.PAGE, DriverTestDataUtil.SIZE)
                ).totalElements
            )
        }
    }
}