package io.simakkoi9.passengerservice.e2e.steps;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import io.simakkoi9.passengerservice.repository.PassengerRepository;
import io.simakkoi9.passengerservice.util.TestDataUtil;
import java.util.Map;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

public class PassengerSteps {

    @LocalServerPort
    private int port;
    
    private Response response;
    private Long passengerId;

    @Autowired
    private PassengerRepository passengerRepository;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = TestDataUtil.BASE_URL_PATH;
        passengerRepository.deleteAll();
    }

    private RequestSpecification requestSpec() {
        return given()
                .header(TestDataUtil.HEADER_ID, TestDataUtil.EXTERNAL_ID)
                .header(TestDataUtil.HEADER_ROLE, TestDataUtil.ROLE_ADMIN)
                .contentType(ContentType.JSON);
    }

    @When("I create a new passenger with details:")
    public void iCreateANewPassengerWithDetails(DataTable dataTable) {
        response = requestSpec()
            .body(TestDataUtil.CREATE_REQUEST)
            .when()
            .post(TestDataUtil.ENDPOINT);

        if (response.statusCode() == HttpStatus.OK.value()) {
            passengerId = response.jsonPath().getLong("id");
        }
    }

    @Then("Passenger should be created successfully")
    public void passengerShouldBeCreatedSuccessfully() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(passengerId).isNotNull();
        
        Passenger createdPassenger = passengerRepository.findById(passengerId).orElse(null);
        assertThat(createdPassenger).isNotNull();
    }

    @When("I create another passenger with the same email")
    public void iCreateAnotherPassengerWithSameEmail() {
        passengerRepository.save(TestDataUtil.getPassenger());
        
        response = requestSpec()
            .body(TestDataUtil.CREATE_REQUEST)
            .when()
            .post(TestDataUtil.ENDPOINT);
    }

    @Then("I should receive a {int} error with a duplicate passenger message")
    public void iShouldReceiveAErrorWithADuplicatePassengerMessage(int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
        response.then()
            .body("status", equalTo(HttpStatus.CONFLICT.value()));
    }

    @Given("a passenger exists in the system")
    public void aPassengerExistsInTheSystem() {
        Passenger savedPassenger = passengerRepository.save(TestDataUtil.getPassenger());
        passengerId = savedPassenger.getId();
    }

    @When("I request the passenger details by ID")
    public void iRequestThePassengerDetailsById() {
        response = requestSpec()
            .when()
            .get(TestDataUtil.ENDPOINT + "/{id}", passengerId);
    }

    @Then("I should receive the passenger's information")
    public void iShouldReceiveThePassengersInformation() {
        response.then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(TestDataUtil.NAME))
            .body("email", equalTo(TestDataUtil.EMAIL));
    }

    @Given("no passenger exists with ID {int}")
    public void noPassengerExistsWithId(int id) {
        passengerId = (long) id;
    }

    @Then("I should receive a {int} error")
    public void iShouldReceiveAError(int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
        
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            response.then()
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    @When("I update the passenger with details:")
    public void iUpdateThePassengerWithDetails(DataTable dataTable) {
        response = requestSpec()
            .body(TestDataUtil.UPDATE_REQUEST)
            .when()
            .patch(TestDataUtil.ENDPOINT + "/{id}", passengerId);
    }

    @Then("the passenger's details should be updated successfully")
    public void thePassengersDetailsShouldBeUpdatedSuccessfully() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        
        Passenger updatedPassenger = passengerRepository.findById(passengerId).orElse(null);
        assertThat(updatedPassenger).isNotNull();
        assertThat(updatedPassenger.getName()).isEqualTo(TestDataUtil.NAME_2);
        assertThat(updatedPassenger.getEmail()).isEqualTo(TestDataUtil.EMAIL_2);
        assertThat(updatedPassenger.getPhone()).isEqualTo(TestDataUtil.PHONE_2);
    }

    @When("I delete the passenger by ID")
    public void iDeleteThePassengerById() {
        response = requestSpec()
            .when()
            .delete(TestDataUtil.ENDPOINT + "/{id}", passengerId);
    }

    @Then("the passenger's status should be set to {string}")
    public void thePassengersStatusShouldBeSetTo(String status) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        
        Passenger deletedPassenger = passengerRepository.findById(passengerId).orElseThrow();
        assertEquals(UserStatus.valueOf(status), deletedPassenger.getStatus());
    }

    @Then("Passenger details should match:")
    public void passengerDetailsShouldMatch(DataTable dataTable) {
        Map<String, String> expectedData = dataTable.asMaps().get(0);
        String expectedName = expectedData.get("name");
        String expectedEmail = expectedData.get("email");
        String expectedPhone = expectedData.get("phone");
        
        response = requestSpec()
            .when()
            .get(TestDataUtil.ENDPOINT + "/{id}", passengerId);
            
        response.then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(expectedName))
            .body("email", equalTo(expectedEmail))
            .body("phone", equalTo(expectedPhone));
    }
    
    @When("I create a passenger with invalid details")
    public void iCreateAPassengerWithInvalidDetails() {
        response = requestSpec()
            .body(TestDataUtil.INVALID_CREATE_REQUEST)
            .when()
            .post(TestDataUtil.ENDPOINT);
    }
    
    @Then("the system should return validation error")
    public void theSystemShouldReturnValidationError() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
    
    @Then("the response should contain {int} errors")
    public void theResponseShouldContainErrors(int errorCount) {
        response.then()
            .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
            .body("errors", hasSize(errorCount));
    }
    
    @When("I request all passengers")
    public void iRequestAllPassengers() {
        response = requestSpec()
            .when()
            .get(TestDataUtil.ENDPOINT);
    }
    
    @Then("the response should contain passengers")
    public void theResponseShouldContainPassengers() {
        response.then()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(1))
            .body("content[0].name", equalTo(TestDataUtil.NAME));
    }
    
    @When("I request passengers with invalid pagination")
    public void iRequestPassengersWithInvalidPagination() {
        response = requestSpec()
            .param("page", TestDataUtil.INVALID_PAGE)
            .param("size", TestDataUtil.INVALID_SIZE)
            .when()
            .get(TestDataUtil.ENDPOINT);
    }
    
    @Then("the response should contain pagination errors")
    public void theResponseShouldContainPaginationErrors() {
        response.then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("errors", hasSize(2))
            .body("errors", containsInAnyOrder(
                TestDataUtil.INVALID_PAGE_MESSAGE,
                TestDataUtil.INVALID_SIZE_MESSAGE
            ));
    }
}

