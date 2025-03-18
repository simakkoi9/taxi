package io.simakkoi9.ratingservice.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.simakkoi9.ratingservice.model.dto.client.RideRequest;
import io.simakkoi9.ratingservice.model.dto.kafka.RidePersonRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.DriverRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.PassengerRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.RatingCreateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.response.AverageRatingResponse;
import io.simakkoi9.ratingservice.model.dto.rest.response.RatingResponse;
import io.simakkoi9.ratingservice.model.entity.Rate;
import io.simakkoi9.ratingservice.model.entity.Rating;
import io.simakkoi9.ratingservice.model.entity.RideStatus;
import java.util.List;

public class TestDataUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static final Long RATING_ID = 1L;
    public static final Long DRIVER_ID = 100L;
    public static final Long PASSENGER_ID = 200L;
    public static final Long NON_EXISTENT_RATING_ID = 999L;
    public static final String RIDE_ID = "qwe61287dgqwe712u";
    public static final String WRONG_RIDE_ID = "qwerty1234qwe";
    public static final String UNCOMPLETED_RIDE_ID = "ajcnbkjabck123123";

    public static final Integer DRIVER_RATE = 5;
    public static final Integer PASSENGER_RATE = 4;
    public static final Integer INVALID_NEGATIVE_RATE = -1;
    public static final Integer INVALID_TOO_HIGH_RATE = 6;
    public static final String DRIVER_COMMENT = "Отличный водитель";
    public static final String PASSENGER_COMMENT = "Хороший пассажир";
    public static final String INVALID_RATE_COMMENT = "Invalid rate";

    public static final String DRIVER_PERSON_ID = "driver_" + DRIVER_ID;
    public static final String PASSENGER_PERSON_ID = "passenger_" + PASSENGER_ID;

    public static final String KAFKA_DRIVER_PERSON_ID = "driver_123";
    public static final String KAFKA_PASSENGER_PERSON_ID = "passenger_456";
    public static final String KAFKA_DRIVER_RATE = "5";
    public static final String KAFKA_PASSENGER_RATE = "4";
    public static final String DRIVER_PREFIX = "driver_";
    public static final String PASSENGER_PREFIX = "passenger_";

    public static final Double DRIVER_AVERAGE_RATE = 4.5;
    public static final Double PASSENGER_AVERAGE_RATE = 3.5;

    public static final int PAGE = 0;
    public static final int SIZE = 10;
    public static final int TOTAL_PAGES = 1;

    public static final String ERROR_DUPLICATE_RATING = String.format("Рейтинг с RideId %s уже существует.", RIDE_ID);
    public static final String ERROR_UNCOMPLETED_RIDE = String.format("Поездка %s еще не завершена.", UNCOMPLETED_RIDE_ID);
    public static final String ERROR_DRIVER_ALREADY_RATED = String.format("Водитель с ID %d уже имеет оценку.", DRIVER_ID);
    public static final String ERROR_PASSENGER_ALREADY_RATED = String.format("Пассажир с ID %d уже имеет оценку.", PASSENGER_ID);
    public static final String ERROR_RATING_NOT_FOUND = String.format("Рейтинг с ID %d не найден.", NON_EXISTENT_RATING_ID);
    public static final String ERROR_DRIVER_NO_RATES = String.format("У водителя с ID %d нет оценок.", DRIVER_ID);
    public static final String ERROR_PASSENGER_NO_RATES = String.format("У пассажира с ID %d нет оценок.", PASSENGER_ID);

    public static final String COMPLETED_RIDE_JSON = "{\"id\":\"" + RIDE_ID + "\",\"status\":\"COMPLETED\"}";
    public static final String UNCOMPLETED_RIDE_JSON = "{\"id\":\"" + RIDE_ID + "\",\"status\":\"EN_ROUTE_TO_DESTINATION\"}";

    public static JsonNode createCompletedRideJson() {
        try {
            return objectMapper.readTree(COMPLETED_RIDE_JSON);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode createUncompletedRideJson() {
        try {
            return objectMapper.readTree(UNCOMPLETED_RIDE_JSON);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Rating createRating() {
        Rating rating = new Rating();
        rating.setId(RATING_ID);
        rating.setRideId(RIDE_ID);
        rating.setRateForDriver(DRIVER_RATE);
        rating.setCommentForDriver(DRIVER_COMMENT);
        rating.setRateForPassenger(PASSENGER_RATE);
        rating.setCommentForPassenger(PASSENGER_COMMENT);
        return rating;
    }

    public static Rating createNewRating() {
        Rating rating = new Rating();
        rating.setRideId(RIDE_ID);
        rating.setRateForDriver(DRIVER_RATE);
        rating.setCommentForDriver(DRIVER_COMMENT);
        rating.setRateForPassenger(PASSENGER_RATE);
        rating.setCommentForPassenger(PASSENGER_COMMENT);
        return rating;
    }
    
    public static Rating createRatingWithoutDriverRate() {
        Rating rating = new Rating();
        rating.setRideId(RIDE_ID);
        rating.setRateForPassenger(PASSENGER_RATE);
        rating.setCommentForPassenger(PASSENGER_COMMENT);
        return rating;
    }
    
    public static Rating createRatingWithoutPassengerRate() {
        Rating rating = new Rating();
        rating.setRideId(RIDE_ID);
        rating.setRateForDriver(DRIVER_RATE);
        rating.setCommentForDriver(DRIVER_COMMENT);
        return rating;
    }
    
    public static RatingCreateRequest createRatingRequest() {
        return new RatingCreateRequest(
                RIDE_ID,
                DRIVER_RATE,
                PASSENGER_RATE,
                DRIVER_COMMENT,
                PASSENGER_COMMENT
        );
    }

    public static RatingCreateRequest createRatingRequestWithoutRates() {
        return new RatingCreateRequest(
                RIDE_ID,
                null,
                null,
                null,
                null
        );
    }

    public static RatingCreateRequest createRatingRequestWithWrongRideId() {
        return new RatingCreateRequest(
                WRONG_RIDE_ID,
                null,
                null,
                null,
                null
        );
    }

    public static RatingCreateRequest createRatingRequestWithUncompletedRide() {
        return new RatingCreateRequest(
                UNCOMPLETED_RIDE_ID,
                null,
                null,
                null,
                null
        );
    }
    
    public static DriverRatingUpdateRequest createDriverRatingUpdateRequest() {
        return new DriverRatingUpdateRequest(DRIVER_RATE, DRIVER_COMMENT);
    }
    
    public static PassengerRatingUpdateRequest createPassengerRatingUpdateRequest() {
        return new PassengerRatingUpdateRequest(PASSENGER_RATE, PASSENGER_COMMENT);
    }
    
    public static RideRequest createCompletedRideRequest() {
        return new RideRequest(RIDE_ID, RideStatus.COMPLETED);
    }
    
    public static RideRequest createUncompletedRideRequest() {
        return new RideRequest(RIDE_ID, RideStatus.EN_ROUTE_TO_DESTINATION);
    }
    
    public static RidePersonRequest createDriverPersonRequest() {
        return new RidePersonRequest("driver", DRIVER_RATE);
    }
    
    public static RidePersonRequest createPassengerPersonRequest() {
        return new RidePersonRequest("passenger", PASSENGER_RATE);
    }
    
    public static List<Rate> createDriverRates() {
        Rate rate1 = new Rate();
        rate1.setPersonId(DRIVER_PERSON_ID);
        rate1.setRate(5);
        
        Rate rate2 = new Rate();
        rate2.setPersonId(DRIVER_PERSON_ID);
        rate2.setRate(4);
        
        return List.of(rate1, rate2);
    }
    
    public static List<Rate> createPassengerRates() {
        Rate rate1 = new Rate();
        rate1.setPersonId(PASSENGER_PERSON_ID);
        rate1.setRate(4);
        
        Rate rate2 = new Rate();
        rate2.setPersonId(PASSENGER_PERSON_ID);
        rate2.setRate(3);
        
        return List.of(rate1, rate2);
    }
    
    public static RatingResponse createRatingResponse(Rating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getRideId(),
                rating.getRateForDriver(),
                rating.getRateForPassenger(),
                rating.getCommentForDriver(),
                rating.getCommentForPassenger()
        );
    }

    public static AverageRatingResponse createDriverAverageRatingResponse() {
        return new AverageRatingResponse(DRIVER_PERSON_ID, DRIVER_AVERAGE_RATE);
    }

    public static AverageRatingResponse createPassengerAverageRatingResponse() {
        return new AverageRatingResponse(PASSENGER_PERSON_ID, PASSENGER_AVERAGE_RATE);
    }

    public static DriverRatingUpdateRequest createInvalidDriverRatingUpdateRequest() {
        return new DriverRatingUpdateRequest(INVALID_NEGATIVE_RATE, INVALID_RATE_COMMENT);
    }

    public static PassengerRatingUpdateRequest createInvalidPassengerRatingUpdateRequest() {
        return new PassengerRatingUpdateRequest(INVALID_TOO_HIGH_RATE, INVALID_RATE_COMMENT);
    }

    public static final String RIDE_REQUEST_JSON =
            """
                {
                    "passengerId": "1",
                    "status": "COMPLETED"
                }
            """;

    public static final String UNCOMPLETED_RIDE_REQUEST_JSON =
            """
                {
                    "passengerId": "2",
                    "status": "ACCEPTED"
                }
            """;

    public static final String RIDE_NOT_FOUND_JSON =
            """
                {
                    "status": 404,
                    "errors": [
                        Requested ride with ID qwerty1234qwe was not found.
                    ]
                }
            """;
}
