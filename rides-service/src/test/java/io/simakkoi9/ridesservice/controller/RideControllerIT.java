package io.simakkoi9.ridesservice.controller;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.simakkoi9.ridesservice.config.TestContainersConfig;
import io.simakkoi9.ridesservice.config.TestKafkaConfig;
import io.simakkoi9.ridesservice.config.TestWebClientConfig;
import io.simakkoi9.ridesservice.model.dto.kafka.KafkaDriverRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.entity.Passenger;
import io.simakkoi9.ridesservice.model.entity.Ride;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import io.simakkoi9.ridesservice.repository.RideRepository;
import io.simakkoi9.ridesservice.service.kafka.KafkaConsumer;
import io.simakkoi9.ridesservice.util.ItDataUtil;
import io.simakkoi9.ridesservice.util.WireMockStubs;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8080)
@Import({
        TestKafkaConfig.class,
        TestContainersConfig.class,
        TestWebClientConfig.class
})
public class RideControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    @Qualifier("testDriverRequestKafkaTemplate")
    private KafkaTemplate<String, KafkaDriverRequest> testKafkaTemplate;

    @BeforeEach
    void setUp() {
        rideRepository.deleteAll();
        RestAssured.port = port;
        RestAssured.basePath = ItDataUtil.BASE_URL_PATH;

        WireMock.reset();

        WireMockStubs.mockPassengerService();
        WireMockStubs.mockInvalidPassengerService();
        WireMockStubs.mockFareService();
    }

    @Test
    void testCreateRide_Valid() throws Exception {
        RideCreateRequest rideCreateRequest = ItDataUtil.getRideCreateRequest();
        Passenger passenger = ItDataUtil.getPassenger();
        String response = given()
                .contentType(ContentType.JSON)
                .body(rideCreateRequest)
                .when()
                    .post(ItDataUtil.RIDES_ENDPOINT)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .path("id");

        Ride createdRide = rideRepository.findById(response).orElse(null);
        assertAll(() -> {
            assertNotNull(createdRide);
            assertEquals(passenger, createdRide.getPassenger());
        });
    }

    @Test
    void testCreateRide_ShouldThrowValidationException() throws Exception {
        RideCreateRequest rideCreateRequest = ItDataUtil.getInvalidRideCreateRequest();
        given()
                .contentType(ContentType.JSON)
                .body(rideCreateRequest)
                .when()
                .post(ItDataUtil.RIDES_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors", hasSize(2));
    }

    @Test
    void testCreateRide_ShouldThrowBusyPassengerException() throws Exception {
        Ride ride = ItDataUtil.getRide();
        ride.setPassenger(ItDataUtil.getPassenger());
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
        RideCreateRequest rideCreateRequest = ItDataUtil.getRideCreateRequest();

        given()
            .contentType(ContentType.JSON)
            .body(rideCreateRequest)
            .when()
                .post(ItDataUtil.RIDES_ENDPOINT)
            .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("status", equalTo(HttpStatus.CONFLICT.value()))
                .body("errors", hasSize(1));
    }

    @Test
    void testCreateRide_ShouldThrowPassengerNotFoundException() throws Exception {
        RideCreateRequest rideCreateRequest = ItDataUtil.getRideCreateRequestWithInvalidPassengerId();
        given()
                .contentType(ContentType.JSON)
                .body(rideCreateRequest)
                .when()
                .post(ItDataUtil.RIDES_ENDPOINT)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()))
                .body("errors", hasSize(1));
    }

    @Test
    void testUpdateRide_Valid() throws Exception {
        RideUpdateRequest rideUpdateRequest = ItDataUtil.getRideUpdateRequest();
        Ride ride = ItDataUtil.getRide();
        Ride createdRide = rideRepository.save(ride);

        given()
            .contentType(ContentType.JSON)
            .body(rideUpdateRequest)
            .when()
                .put(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId())
            .then()
                .statusCode(HttpStatus.OK.value());

        Ride updatedRide = rideRepository.findById(createdRide.getId()).orElse(null);
        assertAll(() -> {
            assertNotNull(updatedRide);
            assertEquals(ItDataUtil.PICKUP_ADDRESS_2, updatedRide.getPickupAddress());
            assertEquals(ItDataUtil.DESTINATION_ADDRESS_2, updatedRide.getDestinationAddress());
            assertEquals(ItDataUtil.COST, updatedRide.getCost());
        });
    }

    @Test
    void testGetAvailableDriver_Valid() throws Exception {
        Ride ride = ItDataUtil.getRide();
        Ride createdRide = rideRepository.save(ride);

        KafkaDriverRequest driverRequest = ItDataUtil.getKafkaDriverRequest();

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            testKafkaTemplate.send(ItDataUtil.KAFKA_TOPIC, ride.getId(), driverRequest);
        });

        await()
            .pollInterval(Duration.ofSeconds(1))
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {
                given()
                    .when()
                        .patch(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId() + "/getDriver")
                    .then()
                        .statusCode(HttpStatus.OK.value());
                Ride updatedRide = rideRepository.findById(createdRide.getId()).orElse(null);
                assertAll(() -> {
                    assertNotNull(updatedRide);
                    assertNotNull(updatedRide.getDriver());
                });
            });
    }

    @Test
    void testGetAvailableDriver_ShouldReturnNotFound_WhenNoDriversAvailable() throws Exception {
        Ride ride = ItDataUtil.getRide();
        Ride createdRide = rideRepository.save(ride);

        given()
            .when()
                .patch(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId() + "/getDriver")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errors", hasSize(1));
        
        Ride unchangedRide = rideRepository.findById(createdRide.getId()).orElse(null);
        assertAll(() -> {
            assertNotNull(unchangedRide);
            assertEquals(RideStatus.CREATED, unchangedRide.getStatus());
            assertNull(unchangedRide.getDriver());
        });
    }

    @Test
    void testGetAvailableDriver_ShouldReturnNotFound_WhenNoDriverResponse() throws Exception {
        Ride ride = ItDataUtil.getRide();
        Ride createdRide = rideRepository.save(ride);

        given()
            .when()
                .patch(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId() + "/getDriver")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errors", hasSize(1));

        Ride unchangedRide = rideRepository.findById(createdRide.getId()).orElse(null);
        assertAll(() -> {
            assertNotNull(unchangedRide);
            assertEquals(RideStatus.CREATED, unchangedRide.getStatus());
            assertNull(unchangedRide.getDriver());
        });
    }

    @Test
    void testChangeRideStatus_Valid() throws Exception {
        Ride ride = ItDataUtil.getRide();
        Ride createdRide = rideRepository.save(ride);

        given()
            .param("status", RideStatus.COMPLETED)
            .when()
                .patch(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId())
            .then()
                .statusCode(HttpStatus.OK.value());

        Ride updatedRide = rideRepository.findById(createdRide.getId()).orElse(null);
        assertAll(() -> {
            assertNotNull(updatedRide);
            assertEquals(RideStatus.COMPLETED, updatedRide.getStatus());
        });
    }

    @Test
    void testChangeRideStatus_ShouldReturnResponse_WhenNewStatusGreaterByOne() throws Exception {
        Ride ride = ItDataUtil.getRide();
        ride.setStatus(RideStatus.EN_ROUTE_TO_PASSENGER);
        Ride createdRide = rideRepository.save(ride);

        given()
            .param("status", RideStatus.EN_ROUTE_TO_DESTINATION)
            .when()
                .patch(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo(RideStatus.EN_ROUTE_TO_DESTINATION.toValue()));

        Ride updatedRide = rideRepository.findById(createdRide.getId()).orElse(null);
        assertAll(() -> {
            assertNotNull(updatedRide);
            assertEquals(RideStatus.EN_ROUTE_TO_DESTINATION, updatedRide.getStatus());
        });
    }

    @Test
    void testChangeRideStatus_ShouldReturnResponse_WhenNewStatusIsImmutable() throws Exception {
        Ride ride = ItDataUtil.getRide();
        Ride createdRide = rideRepository.save(ride);

        given()
            .param("status", RideStatus.CANCELLED_BY_PASSENGER)
            .when()
                .patch(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo(RideStatus.CANCELLED_BY_PASSENGER.toValue()));

        Ride updatedRide = rideRepository.findById(createdRide.getId()).orElse(null);
        assertAll(() -> {
            assertNotNull(updatedRide);
            assertEquals(RideStatus.CANCELLED_BY_PASSENGER, updatedRide.getStatus());
        });
    }

    @Test
    void testChangeRideStatus_ShouldReturnBadRequest_WhenOldStatusIsImmutable() throws Exception {
        Ride ride = ItDataUtil.getRide();
        ride.setStatus(RideStatus.COMPLETED);
        Ride createdRide = rideRepository.save(ride);

        given()
            .param("status", RideStatus.EN_ROUTE_TO_PASSENGER)
            .when()
                .patch(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("errors", hasSize(1));

        Ride unchangedRide = rideRepository.findById(createdRide.getId()).orElse(null);
        assertAll(() -> {
            assertNotNull(unchangedRide);
            assertEquals(RideStatus.COMPLETED, unchangedRide.getStatus());
        });
    }

    @Test
    void testChangeRideStatus_ShouldReturnBadRequest_WhenNewStatusNotImmutableAndDifferenceMoreThanOne()
            throws Exception {
        Ride ride = ItDataUtil.getRide();
        Ride createdRide = rideRepository.save(ride);

        given()
            .param("status", RideStatus.EN_ROUTE_TO_DESTINATION)
            .when()
                .patch(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("errors", hasSize(1));

        Ride unchangedRide = rideRepository.findById(createdRide.getId()).orElse(null);
        assertAll(() -> {
            assertNotNull(unchangedRide);
            assertEquals(RideStatus.CREATED, unchangedRide.getStatus());
        });
    }

    @Test
    void testChangeRideStatus_ShouldReturnBadRequest_WhenOldStatusGreaterThanNew() throws Exception {
        Ride ride = ItDataUtil.getRide();
        ride.setStatus(RideStatus.EN_ROUTE_TO_DESTINATION);
        Ride createdRide = rideRepository.save(ride);

        given()
            .param("status", RideStatus.EN_ROUTE_TO_PASSENGER)
            .when()
                .patch(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("errors", hasSize(1));

        Ride unchangedRide = rideRepository.findById(createdRide.getId()).orElse(null);
        assertAll(() -> {
            assertNotNull(unchangedRide);
            assertEquals(RideStatus.EN_ROUTE_TO_DESTINATION, unchangedRide.getStatus());
        });
    }

    @Test
    void testChangeRideStatus_ShouldReturnNotFound_WhenRideNotExists() throws Exception {
        given()
            .param("status", RideStatus.EN_ROUTE_TO_PASSENGER)
            .when()
                .patch(ItDataUtil.RIDES_ENDPOINT + "/" + ItDataUtil.RIDE_ID)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()))
                .body("errors", hasSize(1));
    }

    @Test
    void testGetRide_Valid() throws Exception {
        Ride ride = ItDataUtil.getRide();
        ride.setStatus(RideStatus.CREATED);
        Ride createdRide = rideRepository.save(ride);

        given()
            .when()
                .get(ItDataUtil.RIDES_ENDPOINT + "/" + createdRide.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(createdRide.getId()));


    }

    @Test
    void testGetRide_ShouldThrowRideNotFoundException() throws Exception {
        given()
            .when()
                .get(ItDataUtil.RIDES_ENDPOINT + "/" + ItDataUtil.RIDE_ID)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testGetAllRides_Valid() throws Exception {
        Ride ride = ItDataUtil.getRide();
        Ride ride2 = ItDataUtil.getAnotherRide();
        rideRepository.saveAll(List.of(ride, ride2));

        given()
            .when()
                .get(ItDataUtil.RIDES_ENDPOINT)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(2))
                .body("totalElements", equalTo(2));
    }

    @Test
    void testGetAllRides_ShouldThrowConstraintViolationException_InvalidPageValues() throws Exception {
        given()
            .param("page", ItDataUtil.INVALID_PAGE)
            .param("size", ItDataUtil.INVALID_SIZE)
            .when()
                .get(ItDataUtil.RIDES_ENDPOINT)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("errors", hasSize(2));
    }
}
