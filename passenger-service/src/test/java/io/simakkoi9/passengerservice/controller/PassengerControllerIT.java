package io.simakkoi9.passengerservice.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.simakkoi9.passengerservice.config.PostgresTestContainer;
import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import io.simakkoi9.passengerservice.repository.PassengerRepository;
import io.simakkoi9.passengerservice.util.TestDataUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresTestContainer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PassengerControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    PassengerRepository passengerRepository;

    @BeforeAll
    static void setUpContainer() {
        PostgresTestContainer.getInstance().start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PostgresTestContainer.getInstance()::getJdbcUrl);
        registry.add("spring.datasource.username", PostgresTestContainer.getInstance()::getUsername);
        registry.add("spring.datasource.password", PostgresTestContainer.getInstance()::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = TestDataUtil.BASE_URI.formatted(port);
    }

    @Test
    @Order(1)
    void createPassenger_ShouldReturnPassengerResponse() throws Exception {
        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.CREATE_REQUEST)
            .when()
                .post()
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(TestDataUtil.CREATE_REQUEST.name()))
                .body("email", equalTo(TestDataUtil.CREATE_REQUEST.email()));
    }

    @Test
    @Order(2)
    void updatePassenger_ShouldReturnUpdatedResponse() throws Exception {
        Passenger savedPassenger = passengerRepository.save(TestDataUtil.PASSENGER);

        given()
            .contentType(ContentType.JSON)
            .body(TestDataUtil.UPDATE_REQUEST)
            .when()
                .patch("/{id}", savedPassenger.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(TestDataUtil.UPDATE_REQUEST.name()))
                .body("email", equalTo(TestDataUtil.UPDATE_REQUEST.email()));
    }

    @Test
    @Order(3)
    void deletePassenger_ShouldSetStatusDeleted() throws Exception {
        Passenger savedPassenger = passengerRepository.save(TestDataUtil.PASSENGER);

        given()
            .when()
                .delete("/{id}", savedPassenger.getId())
            .then()
                .statusCode(HttpStatus.OK.value());

        Passenger deletedPassenger = passengerRepository.findById(savedPassenger.getId()).orElseThrow();
        assertEquals(UserStatus.DELETED, deletedPassenger.getStatus());
    }

    @Test
    @Order(4)
    void getPassenger_ShouldReturnPassengerResponse() throws Exception {
        Passenger savedPassenger = passengerRepository.save(TestDataUtil.PASSENGER);

        given()
            .when()
                .get("/{id}", savedPassenger.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(TestDataUtil.PASSENGER.getName()))
                .body("email", equalTo(TestDataUtil.PASSENGER.getEmail()));
    }

    @Test
    @Order(5)
    void getAllPassengers_ShouldReturnPagedResults() throws Exception {
        passengerRepository.save(TestDataUtil.PASSENGER);

        given()
            .when()
                .get()
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", not(empty()))
                .body("content[0].name", equalTo(TestDataUtil.PASSENGER.getName()));
    }

}
