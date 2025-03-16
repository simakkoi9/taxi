package io.simakkoi9.passengerservice.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.simakkoi9.passengerservice.config.PostgresTestContainer;
import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import io.simakkoi9.passengerservice.repository.PassengerRepository;
import io.simakkoi9.passengerservice.util.MessageKeyConstants;
import io.simakkoi9.passengerservice.util.TestDataUtil;
import java.util.Locale;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(PostgresTestContainer.class)
public class PassengerControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    MessageSource messageSource;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = TestDataUtil.BASE_URI.formatted(port);
        passengerRepository.deleteAll();
    }

    @Test
    void createPassenger_ShouldReturnPassengerResponse() throws Exception {
        int id = given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.CREATE_REQUEST)
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(TestDataUtil.CREATE_REQUEST.name()))
                .body("email", equalTo(TestDataUtil.CREATE_REQUEST.email()))
                .extract()
                .path("id");

        Passenger createdPassenger = passengerRepository.findById((long) id).orElse(null);
        assertNotNull(createdPassenger);
    }

    @Test
    void createPassenger_ShouldThrowDuplicateException() throws Exception {
        passengerRepository.save(TestDataUtil.getPassenger());

        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.CREATE_REQUEST)
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("status", equalTo(HttpStatus.CONFLICT.value()))
                .body(
                        "message",
                        equalTo(
                                messageSource.getMessage(
                                        MessageKeyConstants.DUPLICATE_PASSENGER_FOUND,
                                        new Object[]{TestDataUtil.CREATE_REQUEST.email()},
                                        Locale.ENGLISH
                                )
                        )
                );

    }

    @Test
    void createPassenger_ShouldThrowValidationException_InvalidCreateRequest() throws Exception {
        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.INVALID_CREATE_REQUEST)
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("errors", hasSize(3));
    }

    @Test
    void updatePassenger_ShouldReturnUpdatedResponse() throws Exception {
        Passenger savedPassenger = passengerRepository.save(TestDataUtil.getPassenger());

        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.UPDATE_REQUEST)
            .when()
                .patch("/{id}", savedPassenger.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(TestDataUtil.UPDATE_REQUEST.name()))
                .body("email", equalTo(TestDataUtil.UPDATE_REQUEST.email()));

        Passenger updatedPassenger = passengerRepository.findById(savedPassenger.getId()).orElse(null);
        assertAll(() -> {
            assertNotNull(updatedPassenger);
            assertEquals(TestDataUtil.UPDATE_REQUEST.name(), updatedPassenger.getName());
            assertEquals(TestDataUtil.UPDATE_REQUEST.email(), updatedPassenger.getEmail());
            assertEquals(TestDataUtil.UPDATE_REQUEST.phone(), updatedPassenger.getPhone());
        });
    }

    @Test
    void deletePassenger_ShouldSetStatusDeleted() throws Exception {
        Passenger savedPassenger = passengerRepository.save(TestDataUtil.getPassenger());

        given()
            .when()
                .delete("/{id}", savedPassenger.getId())
            .then()
                .statusCode(HttpStatus.OK.value());

        Passenger deletedPassenger = passengerRepository.findById(savedPassenger.getId()).orElseThrow();
        assertEquals(UserStatus.DELETED, deletedPassenger.getStatus());
    }

    @Test
    void getPassenger_ShouldReturnPassengerResponse() throws Exception {
        Passenger savedPassenger = passengerRepository.save(TestDataUtil.getPassenger());

        given()
            .when()
                .get("/{id}", savedPassenger.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(TestDataUtil.NAME))
                .body("email", equalTo(TestDataUtil.EMAIL));
    }

    @Test
    void getPassenger_ShouldThrowNotFoundException() throws Exception {
        given()
            .when()
                .get("/{id}", TestDataUtil.INVALID_ID)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", equalTo(HttpStatus.NOT_FOUND.value()))
                .body(
                        "message",
                        equalTo(
                                messageSource.getMessage(
                                        MessageKeyConstants.PASSENGER_NOT_FOUND,
                                        new Object[]{TestDataUtil.INVALID_ID},
                                        Locale.ENGLISH
                                )
                        )
                );
    }

    @Test
    void getAllPassengers_ShouldReturnPagedResults() throws Exception {
        passengerRepository.save(TestDataUtil.getPassenger());

        given()
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", not(empty()))
                .body("content[0].name", equalTo(TestDataUtil.NAME));
    }

    @Test
    void getAllPassengers_ShouldThrowValidationException_InvalidPageValues() throws Exception {
        passengerRepository.save(TestDataUtil.getPassenger());

        given()
            .param("page", TestDataUtil.INVALID_PAGE)
            .param("size", TestDataUtil.INVALID_SIZE)
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("errors", hasSize(2))
                .body("errors", containsInAnyOrder(
                        TestDataUtil.INVALID_PAGE_MESSAGE,
                        TestDataUtil.INVALID_SIZE_MESSAGE
                ));
    }

}
