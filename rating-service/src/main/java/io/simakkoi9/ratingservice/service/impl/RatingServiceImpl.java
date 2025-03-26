package io.simakkoi9.ratingservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.simakkoi9.ratingservice.client.RidesClient;
import io.simakkoi9.ratingservice.config.message.MessageConfig;
import io.simakkoi9.ratingservice.exception.DriverAlreadyRatedException;
import io.simakkoi9.ratingservice.exception.DuplicateRatingException;
import io.simakkoi9.ratingservice.exception.NoRatesException;
import io.simakkoi9.ratingservice.exception.PassengerAlreadyRatedException;
import io.simakkoi9.ratingservice.exception.RatingNotFoundException;
import io.simakkoi9.ratingservice.exception.RideJsonProcessingException;
import io.simakkoi9.ratingservice.exception.RideNotFoundException;
import io.simakkoi9.ratingservice.exception.UncompletedRideException;
import io.simakkoi9.ratingservice.model.dto.client.RideRequest;
import io.simakkoi9.ratingservice.model.dto.kafka.RidePersonRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.DriverRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.PassengerRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.request.RatingCreateRequest;
import io.simakkoi9.ratingservice.model.dto.rest.response.AverageRatingResponse;
import io.simakkoi9.ratingservice.model.dto.rest.response.RatingPageResponse;
import io.simakkoi9.ratingservice.model.dto.rest.response.RatingResponse;
import io.simakkoi9.ratingservice.model.entity.Rate;
import io.simakkoi9.ratingservice.model.entity.Rating;
import io.simakkoi9.ratingservice.model.entity.RideStatus;
import io.simakkoi9.ratingservice.model.mapper.RatingMapper;
import io.simakkoi9.ratingservice.repository.RateRepository;
import io.simakkoi9.ratingservice.repository.RatingRepository;
import io.simakkoi9.ratingservice.service.RatingService;
import io.simakkoi9.ratingservice.util.MessageKeyConstants;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class RatingServiceImpl implements RatingService {

    @Inject
    RatingMapper ratingMapper;

    @Inject
    RatingRepository ratingRepository;

    @Inject
    RateRepository rateRepository;

    @Inject
    MessageConfig messageConfig;

    @Inject
    @RestClient
    RidesClient ridesClient;

    @Channel("send-person")
    Emitter<KafkaRecord<String, RidePersonRequest>> emitter;

    @Inject
    TransactionSynchronizationRegistry txSyncRegistry;

    @ConfigProperty(name = "rates.limit")
    int limit;

    @Override
    @Transactional
    public RatingResponse createRating(RatingCreateRequest ratingCreateRequest) {
        if (ratingRepository.existsByRideId(ratingCreateRequest.rideId())) {
            throw new DuplicateRatingException(
                    MessageKeyConstants.DUPLICATE_RATING,
                    messageConfig,
                    ratingCreateRequest.rideId()
            );
        }

        RideRequest rideRequest = getRideByid(ratingCreateRequest.rideId());

        if (!rideRequest.status().equals(RideStatus.COMPLETED)) {
            throw new UncompletedRideException(
                    MessageKeyConstants.UNCOMPLETED_RIDE,
                    messageConfig,
                    ratingCreateRequest.rideId()
            );
        }

        if (ratingCreateRequest.rateForDriver() != null) {
            emit(
                ratingCreateRequest.rideId(),
                new RidePersonRequest("driver", ratingCreateRequest.rateForDriver())
            );
        }
        if (ratingCreateRequest.rateForPassenger() != null) {
            emit(
                ratingCreateRequest.rideId(),
                new RidePersonRequest("passenger", ratingCreateRequest.rateForPassenger())
            );
        }

        Rating rating = ratingMapper.toEntity(ratingCreateRequest);
        rating.persist();
        return ratingMapper.toResponse(rating);
    }

    @Override
    @Transactional
    public RatingResponse setRateForDriver(Long id, DriverRatingUpdateRequest updateRequest) {
        Rating rating = findRatingByIdOrElseThrow(id);
        if (rating.getRateForDriver() != null) {
            throw new DriverAlreadyRatedException(MessageKeyConstants.DRIVER_ALREADY_RATED, messageConfig, id);
        }

        Rating updatedRating = ratingMapper.driverRatingPartialUpdate(updateRequest, rating);

        emit(rating.getRideId(), new RidePersonRequest("driver", updateRequest.rateForDriver()));

        return ratingMapper.toResponse(updatedRating);
    }

    @Override
    @Transactional
    public RatingResponse setRateForPassenger(Long id, PassengerRatingUpdateRequest updateRequest) {
        Rating rating = findRatingByIdOrElseThrow(id);
        if (rating.getRateForPassenger() != null) {
            throw new PassengerAlreadyRatedException(MessageKeyConstants.PASSENGER_ALREADY_RATED, messageConfig, id);
        }

        Rating updatedRating = ratingMapper.passengerRatingPartialUpdate(updateRequest, rating);

        emit(rating.getRideId(), new RidePersonRequest("passenger", updateRequest.rateForPassenger()));

        return ratingMapper.toResponse(updatedRating);
    }

    @Override
    public RatingResponse getRating(Long id) {
        Rating rating = findRatingByIdOrElseThrow(id);
        return ratingMapper.toResponse(rating);
    }

    @Override
    public RatingPageResponse getAllRatings(int page, int size) {
        PanacheQuery<Rating> ratingQuery = Rating.findAll()
                .page(Page.of(page, size));
        List<Rating> ratingList = ratingQuery.list();
        int totalPages = ratingQuery.pageCount();
        List<RatingResponse> ratingResponseList = ratingMapper.toResponseList(ratingList);
        return RatingPageResponse.builder()
                .content(ratingResponseList)
                .currentPage(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public AverageRatingResponse getAverageDriverRating(Long driverId) {
        String personId = "driver_" + driverId;

        List<Rate> driverRateList = rateRepository.getLastRatesByPersonId(personId, limit);

        if (driverRateList.isEmpty()) {
            throw new NoRatesException(MessageKeyConstants.DRIVER_NO_RATES, messageConfig, driverId);
        }

        List<Integer> rateList = driverRateList.stream()
                .map(Rate::getRate)
                .toList();

        Double average = calculateAverageRate(rateList);

        return new AverageRatingResponse(personId, average);
    }

    @Override
    public AverageRatingResponse getAveragePassengerRating(Long passengerId) {
        String personId = "passenger_" + passengerId;

        List<Rate> passengerRateList = rateRepository.getLastRatesByPersonId(personId, limit);

        if (passengerRateList.isEmpty()) {
            throw new NoRatesException(MessageKeyConstants.PASSENGER_NO_RATES, messageConfig, passengerId);
        }

        List<Integer> rateList = passengerRateList.stream()
                .map(Rate::getRate)
                .toList();

        Double average = calculateAverageRate(rateList);

        return new AverageRatingResponse(personId, average);
    }

    private Rating findRatingByIdOrElseThrow(Long id) {
        return (Rating) Rating.findByIdOptional(id).orElseThrow(
                () -> new RatingNotFoundException(MessageKeyConstants.RATING_NOT_FOUND, messageConfig, id)
        );
    }

    private RideRequest getRideByid(String rideId) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = ridesClient.getRideById(rideId);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new RideNotFoundException(MessageKeyConstants.RIDE_NOT_FOUND, messageConfig, rideId);
            }
        }

        RideRequest rideRequest;

        try {
            rideRequest = objectMapper.treeToValue(jsonNode, RideRequest.class);
        } catch (JsonProcessingException e) {
            throw new RideJsonProcessingException(MessageKeyConstants.RIDE_JSON, messageConfig);
        }

        return rideRequest;
    }

    private Double calculateAverageRate(List<Integer> ratingList) {
        double average = ratingList.stream()
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);

        BigDecimal formatted = BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP);

        return formatted.doubleValue();
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public void emit(String key, RidePersonRequest value) {
        txSyncRegistry.registerInterposedSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {

            }

            @Override
            public void afterCompletion(int i) {
                if (i == Status.STATUS_COMMITTED) {
                    emitter.send(KafkaRecord.of(key, value));
                }
            }
        });
    }

}


