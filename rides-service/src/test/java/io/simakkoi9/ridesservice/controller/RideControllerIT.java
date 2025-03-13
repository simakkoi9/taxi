package io.simakkoi9.ridesservice.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.entity.Passenger;
import io.simakkoi9.ridesservice.model.entity.Ride;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import io.simakkoi9.ridesservice.repository.RideRepository;
import io.simakkoi9.ridesservice.util.ItDataUtil;
import io.simakkoi9.ridesservice.util.WireMockStubs;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8080)
@EmbeddedKafka
public class RideControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private RideRepository rideRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = ItDataUtil.BASE_URL_PATH;

        WireMock.reset();

        WireMockStubs.mockPassengerService();

    }

    @AfterEach
    void cleanUp() {
        rideRepository.deleteAll();
    }

    @Test
    void testCreateRide_Valid() {
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
    void testUpdateRide_Valid() {   //Нужно замокать osrm
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
            assertEquals(ItDataUtil.COST_2, updatedRide.getCost());
        });
    }

    @Test
    void testGetAvailableDriver_Valid() {

    }

    @Test
    void testChangeRideStatus_Valid() {
        Ride ride = ItDataUtil.getRide();
        ride.setStatus(RideStatus.CREATED);
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
    void testGetRide_Valid() {
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
    void testGetAllRides_Valid() {

    }
}
