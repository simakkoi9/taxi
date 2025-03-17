package io.simakkoi9.ratingservice.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.simakkoi9.ratingservice.config.TestContainersConfig;
import io.simakkoi9.ratingservice.config.WireMockConfig;
import io.simakkoi9.ratingservice.model.entity.Rating;
import io.simakkoi9.ratingservice.repository.RateRepository;
import io.simakkoi9.ratingservice.repository.RatingRepository;
import io.simakkoi9.ratingservice.util.TestDataUtil;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.TransactionManager;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(RatingController.class)
@QuarkusTestResource(TestContainersConfig.class)
@QuarkusTestResource(WireMockConfig.class)
class RatingControllerIT {
    
    @Inject
    RatingRepository ratingRepository;

    @Inject
    RateRepository rateRepository;

    @Inject
    TransactionManager tm;
    
    @Inject
    EntityManager em;

    @BeforeEach
    void setUp() throws Exception {
        tm.begin();
        ratingRepository.deleteAll();
        rateRepository.deleteAll();
        tm.commit();
    }
    
    @Test
    void testCreateRating_ShouldReturnResponse_WithoutRates() throws Exception {
        Long id = given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.createRatingRequestWithoutRates())
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .jsonPath()
            .getLong("id");

        tm.begin();
        Rating createdRating = ratingRepository.findById(id);
        tm.commit();

        assertAll(() -> {
            assertNotNull(createdRating);
            assertEquals(TestDataUtil.RIDE_ID, createdRating.getRideId());
        });
    }
    
    @Test
    void testCreateRating_ShouldThrowException_UncompletedRide() {
        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.createRatingRequestWithUncompletedRide())
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("message", notNullValue());
    }

    @Test
    void testCreateRating_ShouldThrowException_NotFound() {
        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.createRatingRequestWithWrongRideId())
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .body("message", notNullValue());
    }

    @Test
    void testCreateRating_ShouldThrowException_DuplicateRating() throws Exception {
        tm.begin();
        Rating rating = TestDataUtil.createNewRating();
        ratingRepository.persist(rating);
        em.flush();
        tm.commit();
        em.clear();

        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.createRatingRequest())
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body("message", notNullValue());
    }
    
    @Test
    void testGetAllRatings() throws Exception {
        tm.begin();
        Rating rating = TestDataUtil.createNewRating();
        ratingRepository.persist(rating);
        em.flush();
        tm.commit();
        em.clear();

        given()
            .contentType(ContentType.JSON)
            .queryParam("page", TestDataUtil.PAGE)
            .queryParam("size", TestDataUtil.SIZE)
        .when()
            .get()
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("content", hasSize(1))
            .body("currentPage", equalTo(TestDataUtil.PAGE))
            .body("size", equalTo(TestDataUtil.SIZE));

        tm.begin();
        assertNotNull(ratingRepository.findById(rating.getId()));
        tm.commit();
    }
    
    @Test
    void testGetRating() throws Exception {
        tm.begin();
        Rating rating = TestDataUtil.createNewRating();
        ratingRepository.persist(rating);
        em.flush();
        tm.commit();
        em.clear();
        
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/{id}", rating.getId())
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("id", equalTo(rating.getId().intValue()))
            .body("rideId", equalTo(TestDataUtil.RIDE_ID));
    }

    @Test
    void testGetRating_ShouldThrowException_NotFound() throws Exception {
        given()
            .contentType(ContentType.JSON)
            .when()
                .get("/{id}", TestDataUtil.NON_EXISTENT_RATING_ID)
            .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", notNullValue());
    }
    
    @Test
    void testSetRateForDriver() throws Exception {
        tm.begin();
        Rating rating = TestDataUtil.createRatingWithoutDriverRate();
        ratingRepository.persist(rating);
        em.flush();
        tm.commit();
        em.clear();
        
        var request = TestDataUtil.createDriverRatingUpdateRequest();
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .patch("/{id}/driver/rate", rating.getId())
        .then()
            .statusCode(HttpStatus.SC_OK);
            
        tm.begin();
        Rating updatedRating = ratingRepository.findById(rating.getId());
        tm.commit();
        assertAll(() -> {
            assertEquals(request.rateForDriver(), updatedRating.getRateForDriver());
            assertEquals(request.commentForDriver(), updatedRating.getCommentForDriver());
        });
    }
    
    @Test
    void testSetRateForPassenger() throws Exception {
        tm.begin();
        Rating rating = TestDataUtil.createRatingWithoutPassengerRate();
        ratingRepository.persist(rating);
        em.flush();
        tm.commit();
        em.clear();
        
        var request = TestDataUtil.createPassengerRatingUpdateRequest();
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .patch("/{id}/passenger/rate", rating.getId())
        .then()
            .statusCode(HttpStatus.SC_OK);
            
        tm.begin();
        Rating updatedRating = ratingRepository.findById(rating.getId());
        tm.commit();
        assertAll(() -> {
            assertEquals(request.rateForPassenger(), updatedRating.getRateForPassenger());
            assertEquals(request.commentForPassenger(), updatedRating.getCommentForPassenger());
        });
    }
    
    @Test
    void testGetAverageDriverRating() throws Exception {
        tm.begin();
        TestDataUtil.createDriverRates().forEach(rate -> rateRepository.persist(rate));
        em.flush();
        tm.commit();
        em.clear();
        
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/driver/{id}", TestDataUtil.DRIVER_ID)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("personId", equalTo(TestDataUtil.DRIVER_PERSON_ID))
            .body("averageRating", equalTo(TestDataUtil.DRIVER_AVERAGE_RATE.floatValue()));
    }
    
    @Test
    void testGetAveragePassengerRating() throws Exception {
        tm.begin();
        TestDataUtil.createPassengerRates().forEach(rate -> rateRepository.persist(rate));
        em.flush();
        tm.commit();
        em.clear();
        
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/passenger/{id}", TestDataUtil.PASSENGER_ID)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("personId", equalTo(TestDataUtil.PASSENGER_PERSON_ID))
            .body("averageRating", equalTo(TestDataUtil.PASSENGER_AVERAGE_RATE.floatValue()));
    }



    @Test
    void testSetRateForDriver_ShouldThrowException_AlreadyRated() throws Exception {
        tm.begin();
        Rating rating = TestDataUtil.createNewRating();
        rating.persist();
        em.flush();
        tm.commit();
        em.clear();

        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.createDriverRatingUpdateRequest())
        .when()
            .patch("/{id}/driver/rate", rating.getId())
        .then()
            .statusCode(HttpStatus.SC_CONFLICT)
            .body("message", notNullValue());
    }

    @Test
    void testSetRateForPassenger_ShouldThrowException_AlreadyRated() throws Exception {
        tm.begin();
        Rating rating = TestDataUtil.createNewRating();
        ratingRepository.persist(rating);
        em.flush();
        tm.commit();
        em.clear();

        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.createPassengerRatingUpdateRequest())
        .when()
            .patch("/{id}/passenger/rate", rating.getId())
        .then()
            .statusCode(HttpStatus.SC_CONFLICT)
            .body("message", notNullValue());
    }

    @Test
    void testGetAverageDriverRating_ShouldThrowException_NoRates() throws Exception {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/driver/{id}", TestDataUtil.DRIVER_ID)
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .body("message", notNullValue());
    }

    @Test
    void testGetAveragePassengerRating_ShouldThrowException_NoRates() throws Exception {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/passenger/{id}", TestDataUtil.PASSENGER_ID)
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .body("message", notNullValue());
    }

    @Test
    void testSetRateForDriver_ShouldThrowException_InvalidRate() throws Exception {
        tm.begin();
        Rating rating = TestDataUtil.createRatingWithoutDriverRate();
        ratingRepository.persist(rating);
        em.flush();
        tm.commit();
        em.clear();

        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.createInvalidDriverRatingUpdateRequest())
        .when()
            .patch("/{id}/driver/rate", rating.getId())
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("errors", hasSize(1));
    }

    @Test
    void testSetRateForPassenger_ShouldThrowException_InvalidRate() throws Exception {
        tm.begin();
        Rating rating = TestDataUtil.createRatingWithoutPassengerRate();
        ratingRepository.persist(rating);
        em.flush();
        tm.commit();
        em.clear();

        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.createInvalidPassengerRatingUpdateRequest())
        .when()
            .patch("/{id}/passenger/rate", rating.getId())
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("errors", hasSize(1));
    }
}
