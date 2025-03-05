package io.simakkoi9.passengerservice.util;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PageResponse;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import java.time.LocalDateTime;
import java.util.List;

public class TestDataUtil {

    public static final Long ID = 1L;
    public static final Long INVALID_ID = 2L;
    public static final String BASE_URI = "http://localhost:%d/api/v1/passengers";
    public static final LocalDateTime TIME = LocalDateTime.now();
    public static final int PAGE = 0;
    public static final int SIZE = 10;
    public static final int TOTAL_PAGES = 1;
    public static final long TOTAL_ELEMENTS = 1L;
    public static final int INVALID_PAGE = -1;
    public static final int INVALID_SIZE = 0;
    public static final String INVALID_PAGE_MESSAGE = "Wrong current page number.";
    public static final String INVALID_SIZE_MESSAGE = "Wrong page size.";

    public static String getDuplicatePassengerErrorMessage(String email) {
        return "Passenger with email %s already exists.".formatted(email);
    }

    public static String getPassengerNotFoundErrorMessage(Long id) {
        return "Resource %d not found.".formatted(id);
    }

    public static final PassengerCreateRequest CREATE_REQUEST = new PassengerCreateRequest(
            "name",
            "email@mail.com",
            "+375293453434"
    );

    public static final PassengerCreateRequest INVALID_CREATE_REQUEST = new PassengerCreateRequest(
            "!name",
            "email",
            "12345"
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

    public static <T> PageResponse<T> getPageResponse(List<T> list) {
        return new PageResponse<>(
                list,
                TestDataUtil.SIZE,
                TestDataUtil.PAGE,
                TestDataUtil.TOTAL_PAGES,
                TestDataUtil.TOTAL_ELEMENTS
        );
    }
}
