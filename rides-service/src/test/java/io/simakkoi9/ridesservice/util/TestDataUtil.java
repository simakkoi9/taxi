package io.simakkoi9.ridesservice.util;

import io.simakkoi9.ridesservice.model.dto.feign.PassengerRequest;
import io.simakkoi9.ridesservice.model.dto.kafka.KafkaCarDto;
import io.simakkoi9.ridesservice.model.dto.kafka.KafkaDriverRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.response.PageResponse;
import io.simakkoi9.ridesservice.model.dto.rest.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.Car;
import io.simakkoi9.ridesservice.model.entity.Driver;
import io.simakkoi9.ridesservice.model.entity.Gender;
import io.simakkoi9.ridesservice.model.entity.Passenger;
import io.simakkoi9.ridesservice.model.entity.Ride;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;

public class TestDataUtil {

    public static final String RIDE_ID = "67d03fd48c0f2979907bacc5";
    public static final String PICKUP_ADDRESS = "53.929086, 27.587694";
    public static final String PICKUP_ADDRESS_2 = "55.929086, 27.587694";
    public static final String DESTINATION_ADDRESS = "53.933624, 27.652157";
    public static final String DESTINATION_ADDRESS_2 = "55.933624, 27.652157";
    public static final BigDecimal COST = BigDecimal.valueOf(18.82);
    public static final BigDecimal COST_2 = BigDecimal.valueOf(16.93);
    public static final Long PASSENGER_ID = 1L;
    public static final String PASSENGER_NAME = "passenger";
    public static final String PASSENGER_EMAIL = "passenger@mail.com";
    public static final String PASSENGER_PHONE = "+375290987654";
    public static final Long DRIVER_ID = 1L;
    public static final String DRIVER_NAME = "driver";
    public static final String DRIVER_EMAIL = "driver@mail.com";
    public static final String DRIVER_PHONE = "+375291234567";
    public static final Long CAR_ID = 1L;
    public static final String CAR_NUMBER = "AB1234";
    public static final String CAR_COLOR = "Black";
    public static final String CAR_MODEL = "Model S";
    public static final String CAR_BRAND = "Tesla";
    public static final int PAGE = 0;
    public static final int SIZE = 10;
    public static final int TOTAL_PAGES = 1;
    public static final long TOTAL_ELEMENTS = 1L;
    public static final String PERSON_DRIVER = "driver";
    public static final String PERSON_PASSENGER = "passenger";
    public static final String PERSON_DRIVER_ID = "driver_" + DRIVER_ID;
    public static final String PERSON_PASSENGER_ID = "passenger_" + PASSENGER_ID;
    public static RideCreateRequest getRideCreateRequest() {
        return new RideCreateRequest(PASSENGER_ID, PICKUP_ADDRESS, DESTINATION_ADDRESS);
    }

    public static Ride getRide() {
        Ride ride = new Ride();
        ride.setPickupAddress(PICKUP_ADDRESS);
        ride.setDestinationAddress(DESTINATION_ADDRESS);
        return ride;
    }

    public static PassengerRequest getPassengerRequest() {
        return new PassengerRequest(PASSENGER_ID, PASSENGER_NAME, PASSENGER_EMAIL, PASSENGER_PHONE);
    }

    public static Passenger getPassenger() {
        Passenger passenger = new Passenger();
        passenger.setId(PASSENGER_ID);
        passenger.setName(PASSENGER_NAME);
        passenger.setEmail(PASSENGER_EMAIL);
        passenger.setPhone(PASSENGER_PHONE);
        return passenger;
    }

    public static Car getCar() {
        Car car = new Car();
        car.setId(CAR_ID);
        car.setBrand(CAR_BRAND);
        car.setModel(CAR_MODEL);
        car.setColor(CAR_COLOR);
        car.setNumber(CAR_NUMBER);
        return car;
    }

    public static Driver getDriver() {
        Driver driver = new Driver();
        driver.setId(DRIVER_ID);
        driver.setName(DRIVER_NAME);
        driver.setEmail(DRIVER_EMAIL);
        driver.setPhone(DRIVER_PHONE);
        driver.setGender(Gender.MALE);
        driver.setCar(getCar());
        return driver;
    }

    public static RideResponse getRideResponse(RideStatus rideStatus) {
        if (rideStatus == null) {
            rideStatus = RideStatus.CREATED;
        }
        return new RideResponse(
                RIDE_ID,
                getPassenger(),
                null,
                PICKUP_ADDRESS,
                DESTINATION_ADDRESS,
                COST,
                rideStatus,
                LocalDateTime.now()
        );
    }

    public static RideUpdateRequest getRideUpdateRequest() {
        return new RideUpdateRequest(
                PICKUP_ADDRESS_2,
                DESTINATION_ADDRESS_2
        );
    }

    public static PageRequest getPageRequest() {
        return PageRequest.of(PAGE, SIZE);
    }

    public static <T> PageResponse<T> getPageResponse(List<T> content) {
        return new PageResponse<>(
                content,
                SIZE,
                PAGE,
                TOTAL_PAGES,
                TOTAL_ELEMENTS
        );
    }
}
