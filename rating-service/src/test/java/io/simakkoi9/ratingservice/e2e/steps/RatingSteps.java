package io.simakkoi9.ratingservice.e2e.steps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.simakkoi9.ratingservice.e2e.config.TestState;
import io.simakkoi9.ratingservice.model.dto.rest.request.DriverRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.PassengerRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.entity.Rating;
import io.simakkoi9.ratingservice.repository.RateRepository;
import io.simakkoi9.ratingservice.repository.RatingRepository;
import io.simakkoi9.ratingservice.util.TestDataUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class RatingSteps {

    @Inject
    TestState testState;

    @Inject
    RatingRepository ratingRepository;

    @Inject
    RateRepository rateRepository;

    private Response response;

    @Given("the database is clean")
    public void theDatabaseIsClean() {
        testState.cleanDatabase();
    }

    @Given("a completed ride with ID {string}")
    public void aCompletedRideWithId(String rideId) {
        testState.setCurrentRideId(rideId);
    }

    @When("I create a rating for the ride")
    public void iCreateARatingForTheRide() {
        response = given()
            .contentType("application/json")
            .body(TestDataUtil.createRatingRequestWithoutRates())
        .when()
            .post("/api/v1/ratings");

        if (response.statusCode() == 200) {
            testState.setCurrentRatingId(response.jsonPath().getLong("id"));
        }
    }

    @When("I create a rating for an uncompleted ride")
    public void iCreateARatingForUncompletedRide() {
        response = given()
            .contentType("application/json")
            .body(TestDataUtil.createRatingRequestWithUncompletedRide())
        .when()
            .post("/api/v1/ratings");
    }

    @When("I create a rating for the same ride")
    public void iCreateARatingForTheSameRide() {
        response = given()
            .contentType("application/json")
            .body(TestDataUtil.createRatingRequestWithoutRates())
        .when()
            .post("/api/v1/ratings");
    }

    @Then("the rating is successfully created")
    public void theRatingIsSuccessfullyCreated() {
        response.then()
            .statusCode(200)
            .body("id", notNullValue());
    }

    @And("I can retrieve the created rating")
    public void iCanRetrieveTheCreatedRating() {
        given()
            .contentType("application/json")
        .when()
            .get("/api/v1/ratings/{id}", testState.getCurrentRatingId())
        .then()
            .statusCode(200)
            .body("id", equalTo(testState.getCurrentRatingId().intValue()))
            .body("rideId", equalTo(testState.getCurrentRideId()));
    }

    @Given("an existing rating for ride with ID {string}")
    @Transactional
    public void anExistingRatingForRideWithId(String rideId) {
        Rating rating = TestDataUtil.createNewRating();
        rating.setRideId(rideId);
        ratingRepository.persist(rating);
        testState.setCurrentRideId(rideId);
    }

    @Then("I receive a duplicate rating error")
    public void iReceiveADuplicateRatingError() {
        response.then()
            .statusCode(409)
            .body("message", equalTo(TestDataUtil.ERROR_DUPLICATE_RATING));
    }

    @Then("I receive an uncompleted ride error")
    public void iReceiveAnUncompletedRideError() {
        response.then()
            .statusCode(400)
            .body("message", equalTo(TestDataUtil.ERROR_UNCOMPLETED_RIDE));
    }

    @Then("I receive an error {string}")
    public void iReceiveAnError(String errorMessage) {
        response.then()
            .statusCode(400)
            .body("message", equalTo(errorMessage));
    }

    @Given("an existing rating without driver rate")
    @Transactional
    public void anExistingRatingWithoutDriverRate() {
        Rating rating = TestDataUtil.createRatingWithoutDriverRate();
        ratingRepository.persist(rating);
        testState.setCurrentRatingId(rating.getId());
    }

    @Given("an existing rating without passenger rate")
    @Transactional
    public void anExistingRatingWithoutPassengerRate() {
        Rating rating = TestDataUtil.createRatingWithoutPassengerRate();
        ratingRepository.persist(rating);
        testState.setCurrentRatingId(rating.getId());
    }

    @When("I set driver rate to {string} with comment {string}")
    public void iSetDriverRateToWithComment(String rate, String comment) {
        response = given()
            .contentType("application/json")
            .body(new DriverRatingUpdateRequest(Integer.parseInt(rate), comment))
        .when()
            .patch("/api/v1/ratings/{id}/driver/rate", testState.getCurrentRatingId());
    }

    @When("I set passenger rate to {string} with comment {string}")
    public void iSetPassengerRateToWithComment(String rate, String comment) {
        response = given()
            .contentType("application/json")
            .body(new PassengerRatingUpdateRequest(Integer.parseInt(rate), comment))
        .when()
            .patch("/api/v1/ratings/{id}/passenger/rate", testState.getCurrentRatingId());
    }

    @Then("the driver rate is successfully updated")
    @Transactional
    public void theDriverRateIsSuccessfullyUpdated() {
        response.then().statusCode(200);
        Rating rating = ratingRepository.findById(testState.getCurrentRatingId());
        assertNotNull(rating.getRateForDriver());
        assertNotNull(rating.getCommentForDriver());
    }

    @Then("the passenger rate is successfully updated")
    @Transactional
    public void thePassengerRateIsSuccessfullyUpdated() {
        response.then().statusCode(200);
        Rating rating = ratingRepository.findById(testState.getCurrentRatingId());
        assertNotNull(rating.getRateForPassenger());
        assertNotNull(rating.getCommentForPassenger());
    }

    @Then("I receive a validation error for invalid rate")
    public void iReceiveAValidationErrorForInvalidRate() {
        response.then()
            .statusCode(400)
            .body("errors", hasSize(1));
    }

    @Given("multiple ratings exist for driver with ID {string}")
    @Transactional
    public void multipleRatingsExistForDriverWithId(String driverId) {
        TestDataUtil.createDriverRates().forEach(rate -> rateRepository.persist(rate));
        testState.setCurrentDriverId(driverId);
    }

    @Given("multiple ratings exist for passenger with ID {string}")
    @Transactional
    public void multipleRatingsExistForPassengerWithId(String passengerId) {
        TestDataUtil.createPassengerRates().forEach(rate -> rateRepository.persist(rate));
        testState.setCurrentDriverId(passengerId);
    }

    @When("I request the average rating for the driver")
    public void iRequestTheAverageRatingForTheDriver() {
        response = given()
            .contentType("application/json")
        .when()
            .get("/api/v1/ratings/driver/{id}", testState.getCurrentDriverId());
    }

    @When("I request the average rating for the passenger")
    public void iRequestTheAverageRatingForThePassenger() {
        response = given()
            .contentType("application/json")
        .when()
            .get("/api/v1/ratings/passenger/{id}", testState.getCurrentDriverId());
    }

    @Then("I receive the driver's average rating {string}")
    public void iReceiveTheDriversAverageRating(String averageRating) {
        response.then()
            .statusCode(200)
            .body("averageRating", equalTo(Float.parseFloat(averageRating)));
    }

    @Then("I receive the passenger's average rating {string}")
    public void iReceiveThePassengersAverageRating(String averageRating) {
        response.then()
            .statusCode(200)
            .body("averageRating", equalTo(Float.parseFloat(averageRating)));
    }

    @Given("an uncompleted ride with ID {string}")
    public void anUncompletedRideWithId(String rideId) {
        testState.setCurrentRideId(rideId);
    }
}
