package io.simakkoi9.ratingservice.service;

import io.simakkoi9.ratingservice.model.dto.request.RatingCreateRequest;
import io.simakkoi9.ratingservice.model.dto.response.AverageRatingResponse;
import io.simakkoi9.ratingservice.model.dto.response.RatingResponse;

import java.util.List;

public interface RatingService {
    RatingResponse createRating(RatingCreateRequest ratingCreateRequest);

    RatingResponse setRateForDriver(Long id);

    RatingResponse setRateForPassenger(Long id);

    RatingResponse getRating(Long id);

    List<RatingResponse> getAllRatings(int page, int size);

    AverageRatingResponse getAverageRatingForDriver(Long driverId);

    AverageRatingResponse getAverageRatingForPassenger(Long passengerId);

}
