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
    public static final String BASE_URI = "http://localhost";
    public static final String BASE_URL_PATH = "/api/v1";
    public static final String ENDPOINT = "/passengers";
    public static final LocalDateTime TIME = LocalDateTime.now();
    public static final int PAGE = 0;
    public static final int SIZE = 10;
    public static final int TOTAL_PAGES = 1;
    public static final long TOTAL_ELEMENTS = 1L;
    public static final int INVALID_PAGE = -1;
    public static final int INVALID_SIZE = 0;
    public static final String INVALID_PAGE_MESSAGE = "Wrong current page number.";
    public static final String INVALID_SIZE_MESSAGE = "Wrong page size.";
    public static final String NAME = "name";
    public static final String EMAIL = "email@mail.com";
    public static final String PHONE = "+375293453434";
    public static final String NAME_2 = "otherName";
    public static final String EMAIL_2 = "otherEmail@mail.com";
    public static final String PHONE_2 = "+375298765432";

    public static String getDuplicatePassengerErrorMessage(String email) {
        return "Passenger with email %s already exists.".formatted(email);
    }

    public static String getPassengerNotFoundErrorMessage(Long id) {
        return "Resource %d not found.".formatted(id);
    }

    public static final PassengerCreateRequest CREATE_REQUEST = new PassengerCreateRequest(
            NAME,
            EMAIL,
            PHONE
    );

    public static final PassengerCreateRequest INVALID_CREATE_REQUEST = new PassengerCreateRequest(
            "!name",
            "email",
            "12345"
    );

    public static final PassengerUpdateRequest UPDATE_REQUEST = new PassengerUpdateRequest(
            NAME_2,
            EMAIL_2,
            PHONE_2
    );

    public static final PassengerResponse RESPONSE = new PassengerResponse(
            ID,
            NAME,
            EMAIL,
            PHONE,
            TIME
    );

    public static final PassengerResponse UPDATED_RESPONSE = new PassengerResponse(
            ID,
            NAME_2,
            EMAIL_2,
            PHONE_2,
            TIME
    );

    public static Passenger getPassenger() {
        Passenger passenger = new Passenger();
        passenger.setName(NAME);
        passenger.setEmail(EMAIL);
        passenger.setPhone(PHONE);
        passenger.setStatus(UserStatus.ACTIVE);
        passenger.setCreatedAt(TIME);
        return passenger;
    }

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
