package io.simakkoi9.passengerservice.util;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import java.time.LocalDateTime;

public class TestDataUtil {
    public static final Long ID = 1L;
    public static final String BASE_URI = "http://localhost:%d/api/v1/passengers";
    public static final LocalDateTime TIME = LocalDateTime.now();

    public static final PassengerCreateRequest CREATE_REQUEST = new PassengerCreateRequest(
            "name",
            "email@mail.com",
            "+375293453434"
    );

    public static final PassengerUpdateRequest UPDATE_REQUEST = new PassengerUpdateRequest(
            "otherName",
            "otherEmail@mail.com",
            "+375298765432"
    );

    public static final PassengerResponse RESPONSE = new PassengerResponse(
            ID,
            "name",
            "email@mail.com",
            "+375293453434",
            TIME
    );

    public static final PassengerResponse UPDATED_RESPONSE = new PassengerResponse(
            ID,
            "otherName",
            "otherEmail@mail.com",
            "+375298765432",
            TIME
    );

    public static final Passenger PASSENGER = new Passenger(
            ID,
            "name",
            "email@mail.com",
            "+375293453434",
            UserStatus.ACTIVE,
            TIME
    );
}
