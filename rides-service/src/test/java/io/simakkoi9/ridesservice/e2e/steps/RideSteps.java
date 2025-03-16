package io.simakkoi9.ridesservice.e2e.steps;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.simakkoi9.ridesservice.model.dto.kafka.KafkaDriverRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.entity.Ride;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import io.simakkoi9.ridesservice.repository.RideRepository;
import io.simakkoi9.ridesservice.util.ItDataUtil;
import io.simakkoi9.ridesservice.util.WireMockStubs;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;

public class RideSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    @Qualifier("testDriverRequestKafkaTemplate")
    private KafkaTemplate<String, KafkaDriverRequest> testKafkaTemplate;

    private Response response;
    private String currentRideId;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = ItDataUtil.BASE_URL_PATH;
        rideRepository.deleteAll();
        WireMockStubs.mockPassengerService();
    }

    @Given("a passenger exists with following details:")
    public void aPassengerExistsWithFollowingDetails(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        assertThat(data)
            .containsEntry("id", ItDataUtil.PASSENGER_ID.toString())
            .containsEntry("name", ItDataUtil.PASSENGER_NAME)
            .containsEntry("email", ItDataUtil.PASSENGER_EMAIL)
            .containsEntry("phone", ItDataUtil.PASSENGER_PHONE);
    }

    @Given("a passenger exists in the system")
    public void aPassengerExistsInTheSystem() {
        WireMockStubs.mockPassengerService();
    }

    @Given("route service is active")
    public void routeServiceIsActive() {
        WireMockStubs.mockFareService();
    }

    @When("I create a new ride")
    public void iCreateANewRide() {
        RideCreateRequest request = ItDataUtil.getRideCreateRequest();
        response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(ItDataUtil.RIDES_ENDPOINT);

        if (response.statusCode() == HttpStatus.OK.value()) {
            currentRideId = response.jsonPath().getString("id");
        }
    }

    @When("I create a ride with invalid coordinates")
    public void iCreateARideWithInvalidCoordinates() {
        RideCreateRequest request = ItDataUtil.getInvalidRideCreateRequest();
        response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(ItDataUtil.RIDES_ENDPOINT);
    }

    @When("I create a ride with invalid passenger")
    public void iCreateARideWithInvalidPassenger() {
        RideCreateRequest request = ItDataUtil.getRideCreateRequestWithInvalidPassengerId();
        response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(ItDataUtil.RIDES_ENDPOINT);
    }

    @Then("the ride should be created successfully")
    public void theRideShouldBeCreatedSuccessfully() {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(currentRideId).isNotNull();
    }

    @Then("the ride status should be {string}")
    public void theRideStatusShouldBe(String status) {
        Ride ride = rideRepository.findById(currentRideId).orElseThrow();
        assertThat(ride.getStatus()).isEqualTo(RideStatus.valueOf(status));
    }

    @Then("the ride cost should be {double}")
    public void theRideCostShouldBe(double cost) {
        Ride ride = rideRepository.findById(currentRideId).orElseThrow();
        assertThat(ride.getCost()).isEqualTo(BigDecimal.valueOf(cost));
    }

    @When("I request a driver for the ride")
    public void iRequestADriverForTheRide() {
        KafkaDriverRequest driverRequest = ItDataUtil.getKafkaDriverRequest();
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            testKafkaTemplate.send(ItDataUtil.KAFKA_TOPIC, currentRideId, driverRequest);
        });

        await()
            .pollInterval(Duration.ofSeconds(1))
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {
                response = given()
                    .when()
                    .patch(ItDataUtil.RIDES_ENDPOINT + "/" + currentRideId + "/getDriver");
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            });
    }

    @When("I request an unavailable driver for the ride")
    public void iRequestAnUnavailableDriverForTheRide() {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            testKafkaTemplate.send(new ProducerRecord<>(ItDataUtil.KAFKA_TOPIC, currentRideId, null));
        });

        await()
                .pollInterval(Duration.ofSeconds(1))
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    response = given()
                            .when()
                            .patch(ItDataUtil.RIDES_ENDPOINT + "/" + currentRideId + "/getDriver");
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Then("a driver should be assigned with following details:")
    public void aDriverShouldBeAssignedWithFollowingDetails(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        Ride ride = rideRepository.findById(currentRideId).orElseThrow();
        
        assertThat(ride.getDriver())
            .isNotNull()
            .satisfies(driver -> {
                assertThat(driver.getId()).isEqualTo(Long.parseLong(data.get("id")));
                assertThat(driver.getName()).isEqualTo(data.get("name"));
                assertThat(driver.getEmail()).isEqualTo(data.get("email"));
                assertThat(driver.getPhone()).isEqualTo(data.get("phone"));
                assertThat(driver.getCar().getBrand()).isEqualTo(data.get("carBrand"));
                assertThat(driver.getCar().getModel()).isEqualTo(data.get("carModel"));
                assertThat(driver.getCar().getColor()).isEqualTo(data.get("carColor"));
                assertThat(driver.getCar().getNumber()).isEqualTo(data.get("carNumber"));
            });
    }

    @When("I update the ride status to {string}")
    public void iUpdateTheRideStatusTo(String status) {
        response = given()
            .param("status", status)
            .when()
            .patch(ItDataUtil.RIDES_ENDPOINT + "/" + currentRideId);
    }

    @When("I update the ride with the following details:")
    public void iUpdateTheRideWithTheFollowingDetails(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        RideUpdateRequest request = new RideUpdateRequest(
            data.get("pickupAddress"),
            data.get("destinationAddress")
        );

        response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .put(ItDataUtil.RIDES_ENDPOINT + "/" + currentRideId);
    }

    @Then("the ride pickup address should be {string}")
    public void theRidePickupAddressShouldBe(String address) {
        Ride ride = rideRepository.findById(currentRideId).orElseThrow();
        assertThat(ride.getPickupAddress()).isEqualTo(address);
    }

    @Then("the ride destination address should be {string}")
    public void theRideDestinationAddressShouldBe(String address) {
        Ride ride = rideRepository.findById(currentRideId).orElseThrow();
        assertThat(ride.getDestinationAddress()).isEqualTo(address);
    }

    @Then("the system should return validation error")
    public void theSystemShouldReturnValidationError() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Then("the response should contain {int} error(s)")
    public void theResponseShouldContainErrors(int errorCount) {
        assertThat(response.jsonPath().getList("errors")).hasSize(errorCount);
    }

    @Given("a passenger with id {string} has an active ride with status {string}")
    public void aPassengerHasAnActiveRideWithStatus(String passengerId, String status) {
        Ride ride = ItDataUtil.getRide();
        ride.setPassenger(ItDataUtil.getPassenger());
        ride.setStatus(RideStatus.valueOf(status));
        rideRepository.save(ride);
    }

    @Then("the system should return conflict error")
    public void theSystemShouldReturnConflictError() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Then("the system should return not found error")
    public void theSystemShouldReturnNotFoundError() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Then("the system should return no available drivers error")
    public void theSystemShouldReturnNoAvailableDriversError() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Then("the ride status should remain {string}")
    public void theRideStatusShouldRemain(String status) {
        Ride ride = rideRepository.findById(currentRideId).orElseThrow();
        assertThat(ride.getStatus()).isEqualTo(RideStatus.valueOf(status));
    }

    @When("I try to update status from {string} to {string}")
    public void iTryToUpdateStatusFromTo(String fromStatus, String toStatus) {
        Ride ride = rideRepository.findById(currentRideId).orElseThrow();
        assertThat(ride.getStatus()).isEqualTo(RideStatus.valueOf(fromStatus));

        response = given()
            .param("status", toStatus)
            .when()
            .patch(ItDataUtil.RIDES_ENDPOINT + "/" + currentRideId);
    }

    @Then("the system should return invalid status transition error")
    public void theSystemShouldReturnInvalidStatusTransitionError() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errors[0]"))
            .contains("Incorrect status assignment: " + RideStatus.EN_ROUTE_TO_DESTINATION);
    }

    @Given("the ride status is {string}")
    public void theRideStatusIs(String status) {
        Ride ride = rideRepository.findById(currentRideId).orElseThrow();
        ride.setStatus(RideStatus.valueOf(status));
        rideRepository.save(ride);

        Ride updatedRide = rideRepository.findById(currentRideId).orElseThrow();
        assertThat(updatedRide.getStatus()).isEqualTo(RideStatus.valueOf(status));
    }

    @Given("the following rides exist in the system:")
    public void theFollowingRidesExistInTheSystem(DataTable dataTable) {
        List<Map<String, String>> rides = dataTable.asMaps();
        
        rides.forEach(rideData -> {
            Ride ride = new Ride();
            ride.setPickupAddress(rideData.get("pickupAddress"));
            ride.setDestinationAddress(rideData.get("destinationAddress"));
            ride.setStatus(RideStatus.valueOf(rideData.get("status")));
            ride.setPassenger(ItDataUtil.getPassenger());
            ride.setCost(ItDataUtil.COST);
            
            rideRepository.save(ride);
        });

        assertThat(rideRepository.findAll())
            .hasSize(rides.size());
    }

    @When("I request rides with page {string} and size {string}")
    public void iRequestRidesWithPageAndSize(String page, String size) {
        response = given()
            .param("page", page)
            .param("size", size)
            .when()
            .get(ItDataUtil.RIDES_ENDPOINT);
    }
    
    @Then("the response should contain pagination information")
    public void theResponseShouldContainPaginationInformation() {
        assertThat(response.jsonPath().getList("content")).isNotNull();
        assertThat(response.jsonPath().getLong("totalElements")).isNotNull();
        assertThat(response.jsonPath().getInt("totalPages")).isNotNull();
        assertThat(response.jsonPath().getInt("size")).isNotNull();
        assertThat(response.jsonPath().getInt("page")).isNotNull();
    }

    @Then("the response should contain {int} rides in content")
    public void theResponseShouldContainRidesInContent(int expectedCount) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("content")).hasSize(expectedCount);
        assertThat(response.jsonPath().getInt("totalElements")).isEqualTo(expectedCount);
    }

    @Given("I have created a ride")
    public void iHaveCreatedARide() {
        Ride ride = ItDataUtil.getRide();
        ride.setPassenger(ItDataUtil.getPassenger());
        Ride savedRide = rideRepository.save(ride);
        currentRideId = savedRide.getId();
    }

    @When("I update ride addresses")
    public void iUpdateRideAddresses() {
        RideUpdateRequest request = ItDataUtil.getRideUpdateRequest();
        response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .put(ItDataUtil.RIDES_ENDPOINT + "/" + currentRideId);
    }

    @Then("the ride addresses should be updated")
    public void theRideAddressesShouldBeUpdated() {
        Ride ride = rideRepository.findById(currentRideId).orElseThrow();
        assertThat(ride.getPickupAddress()).isEqualTo(ItDataUtil.PICKUP_ADDRESS_2);
        assertThat(ride.getDestinationAddress()).isEqualTo(ItDataUtil.DESTINATION_ADDRESS_2);
    }

    @Given("a passenger has an active ride")
    public void aPassengerHasAnActiveRide() {
        Ride ride = ItDataUtil.getRide();
        ride.setPassenger(ItDataUtil.getPassenger());
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }

    @Given("two rides exist in the system")
    public void twoRidesExistInTheSystem() {
        Ride ride1 = ItDataUtil.getRide();
        Ride ride2 = ItDataUtil.getAnotherRide();
        ride1.setPassenger(ItDataUtil.getPassenger());
        ride2.setPassenger(ItDataUtil.getPassenger());
        rideRepository.saveAll(List.of(ride1, ride2));
    }

    @When("I request rides with invalid pagination")
    public void iRequestRidesWithInvalidPagination() {
        response = given()
            .param("page", ItDataUtil.INVALID_PAGE)
            .param("size", ItDataUtil.INVALID_SIZE)
            .when()
            .get(ItDataUtil.RIDES_ENDPOINT);
    }

    @When("I request rides with valid pagination")
    public void iRequestRidesWithValidPagination() {
        response = given()
            .param("page", 0)
            .param("size", 10)
            .when()
            .get(ItDataUtil.RIDES_ENDPOINT);
    }

    @Then("a driver should be assigned")
    public void aDriverShouldBeAssigned() {
        Ride ride = rideRepository.findById(currentRideId).orElseThrow();
        assertThat(ride.getDriver())
            .isNotNull()
            .satisfies(driver -> {
                assertThat(driver.getId()).isEqualTo(ItDataUtil.DRIVER_ID);
                assertThat(driver.getName()).isEqualTo(ItDataUtil.DRIVER_NAME);
                assertThat(driver.getEmail()).isEqualTo(ItDataUtil.DRIVER_EMAIL);
                assertThat(driver.getPhone()).isEqualTo(ItDataUtil.DRIVER_PHONE);
                assertThat(driver.getCar().getBrand()).isEqualTo(ItDataUtil.CAR_BRAND);
                assertThat(driver.getCar().getModel()).isEqualTo(ItDataUtil.CAR_MODEL);
                assertThat(driver.getCar().getColor()).isEqualTo(ItDataUtil.CAR_COLOR);
                assertThat(driver.getCar().getNumber()).isEqualTo(ItDataUtil.CAR_NUMBER);
            });
    }


}
