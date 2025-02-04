package io.simakkoi9.ratingservice.service.impl;

import io.simakkoi9.ratingservice.model.dto.request.RatingCreateRequest;
import io.simakkoi9.ratingservice.model.dto.response.AverageRatingResponse;
import io.simakkoi9.ratingservice.model.dto.response.RatingResponse;
import io.simakkoi9.ratingservice.service.RatingService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RatingServiceImpl implements RatingService {

    @Override
    public RatingResponse createRating(RatingCreateRequest ratingCreateRequest) {
        return null;
    }

    @Override
    public RatingResponse setRateForDriver(Long id) {
        return null;
    }

    @Override
    public RatingResponse setRateForPassenger(Long id) {
        return null;
    }

    @Override
    public RatingResponse getRating(Long id) {
        return null;
    }

    @Override
    public List<RatingResponse> getAllRatings(int page, int size) {
        return null;
    }

    @Override
    public AverageRatingResponse getAverageRatingForDriver(Long driverId) {
        return null;
    }

    @Override
    public AverageRatingResponse getAverageRatingForPassenger(Long passengerId) {
        return null;
    }
}
