package io.simakkoi9.ridesservice.util;

import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.entity.Passenger;
import io.simakkoi9.ridesservice.model.entity.Ride;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import java.math.BigDecimal;

public class ItDataUtil {
    public static final String BASE_URL_PATH = "/api/v1";
    public static final String RIDES_ENDPOINT = "/rides";
    public static final String OSRM_URL = "http://router.project-osrm.org/route/v1/driving";

    public static final String RIDE_ID = "67d03fd48c0f2979907bacc5";
    public static final Long PASSENGER_ID = 1L;
    public static final String PASSENGER_NAME = "passenger";
    public static final String PASSENGER_EMAIL = "passenger@mail.com";
    public static final String PASSENGER_PHONE = "+375290987654";
    public static final String PICKUP_ADDRESS = "53.929086, 27.587694";
    public static final String PICKUP_ADDRESS_2 = "55.929086, 27.587694";
    public static final String DESTINATION_ADDRESS = "53.933624, 27.652157";
    public static final String DESTINATION_ADDRESS_2 = "55.933624, 27.652157";
    public static final BigDecimal COST = BigDecimal.valueOf(18.82);
    public static final BigDecimal COST_2 = BigDecimal.valueOf(16.93);
    public static final int INVALID_PAGE = -1;
    public static final int INVALID_SIZE = 0;

    public static RideCreateRequest getRideCreateRequest() {
        return new RideCreateRequest(
                PASSENGER_ID,
                PICKUP_ADDRESS,
                DESTINATION_ADDRESS
        );
    }

    public static RideCreateRequest getInvalidRideCreateRequest() {
        return new RideCreateRequest(
                PASSENGER_ID,
                "123",
                "123"
        );
    }

    public static Ride getRide() {
        Ride ride = new Ride();
        ride.setPickupAddress(PICKUP_ADDRESS);
        ride.setDestinationAddress(DESTINATION_ADDRESS);
        ride.setStatus(RideStatus.CREATED);
        return ride;
    }

    public static Ride getAnotherRide() {
        Ride ride = new Ride();
        ride.setPickupAddress(PICKUP_ADDRESS_2);
        ride.setDestinationAddress(DESTINATION_ADDRESS_2);
        return ride;
    }

    public static Passenger getPassenger() {
        Passenger passenger = new Passenger();
        passenger.setId(PASSENGER_ID);
        passenger.setName(PASSENGER_NAME);
        passenger.setEmail(PASSENGER_EMAIL);
        passenger.setPhone(PASSENGER_PHONE);
        return passenger;
    }

    public static RideUpdateRequest getRideUpdateRequest() {
        return new RideUpdateRequest(
                PICKUP_ADDRESS_2,
                DESTINATION_ADDRESS_2
        );
    }


}
