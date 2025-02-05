package io.simakkoi9.ratingservice.service;

import io.simakkoi9.ratingservice.model.dto.request.DriverRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.request.PassengerRatingUpdateRequest;
import io.simakkoi9.ratingservice.model.dto.request.RatingCreateRequest;
import io.simakkoi9.ratingservice.model.dto.response.AverageRatingResponse;
import io.simakkoi9.ratingservice.model.dto.response.RatingPageResponse;
import io.simakkoi9.ratingservice.model.dto.response.RatingResponse;

import java.util.List;

public interface RatingService {
    RatingResponse createRating(RatingCreateRequest ratingCreateRequest);

    RatingResponse setRateForDriver(Long id, DriverRatingUpdateRequest updateRequest);

    RatingResponse setRateForPassenger(Long id, PassengerRatingUpdateRequest updateRequest);

    RatingResponse getRating(Long id);

    RatingPageResponse getAllRatings(int page, int size);

    AverageRatingResponse getAverageRatingForDriver(Long driverId);

    AverageRatingResponse getAverageRatingForPassenger(Long passengerId);

}
