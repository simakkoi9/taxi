package io.simakkoi9.ratingservice.service.impl;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.simakkoi9.ratingservice.config.MessageConfig;
import io.simakkoi9.ratingservice.exception.DriverAlreadyRatedException;
import io.simakkoi9.ratingservice.exception.DuplicateRatingException;
import io.simakkoi9.ratingservice.exception.NoRatesException;
import io.simakkoi9.ratingservice.exception.PassengerAlreadyRatedException;
import io.simakkoi9.ratingservice.exception.RatingNotFoundException;
import io.simakkoi9.ratingservice.model.dto.request.DriverRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.request.PassengerRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.request.RatingCreateRequest;
import io.simakkoi9.ratingservice.model.dto.response.AverageRatingResponse;
import io.simakkoi9.ratingservice.model.dto.response.RatingPageResponse;
import io.simakkoi9.ratingservice.model.dto.response.RatingResponse;
import io.simakkoi9.ratingservice.model.entity.Rating;
import io.simakkoi9.ratingservice.model.mapper.RatingMapper;
import io.simakkoi9.ratingservice.repository.RatingRepository;
import io.simakkoi9.ratingservice.service.RatingService;
import io.simakkoi9.ratingservice.util.MessageKeyConstants;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class RatingServiceImpl implements RatingService {

    @Inject
    RatingMapper ratingMapper;
    @Inject
    RatingRepository ratingRepository;
    @Inject
    MessageConfig messageConfig;

    @Override
    @Transactional
    public RatingResponse createRating(RatingCreateRequest ratingCreateRequest) {
        if (ratingRepository.existsByRideId(ratingCreateRequest.rideId())) {
            throw new DuplicateRatingException(MessageKeyConstants.DUPLICATE_RATING, messageConfig, ratingCreateRequest.rideId());
        }
        //Нужна еще проверка наличия в сервисе поездок
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
        List<Long> driverRideIdList = List.of(1L, 2L);  //Cписок из сервиса поездок

        List<Rating> ratingList = ratingRepository.findAllRatingsByRideIdIn(driverRideIdList);

        if (ratingList.isEmpty()) {
            throw new RatingNotFoundException(MessageKeyConstants.RATING_NOT_FOUND, messageConfig, driverRideIdList);
        }

        Double average = ratingList.stream()
                            .map(Rating::getRateForDriver)
                            .filter(Objects::nonNull)
                            .mapToDouble(Integer::doubleValue)
                            .average()
                            .orElseThrow(
                                    () -> new NoRatesException(MessageKeyConstants.DRIVER_NO_RATES, messageConfig, driverId)
                            );

        return new AverageRatingResponse(driverId, average);
    }

    @Override
    public AverageRatingResponse getAveragePassengerRating(Long passengerId) {
        List<Long> passengerRideIdList = List.of(1L, 3L);  //Cписок из сервиса поездок

        List<Rating> passengerList = ratingRepository.findAllRatingsByRideIdIn(passengerRideIdList);

        if (passengerList.isEmpty()){
            throw new RatingNotFoundException(MessageKeyConstants.RATING_NOT_FOUND, messageConfig, passengerRideIdList);
        }

        Double average = passengerList.stream()
                .map(Rating::getRateForPassenger)
                .filter(Objects::nonNull)
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElseThrow(
                        () -> new NoRatesException(MessageKeyConstants.PASSENGER_NO_RATES, messageConfig, passengerId)
                );

        return new AverageRatingResponse(passengerId, average);
    }

    private Rating findRatingByIdOrElseThrow(Long id) {

        return (Rating) Rating.findByIdOptional(id).orElseThrow(
                () -> new RatingNotFoundException(MessageKeyConstants.RATING_NOT_FOUND, messageConfig, id)
        );
    }

}
