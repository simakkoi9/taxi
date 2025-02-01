package io.simakkoi9.ridesservice.service;

import io.simakkoi9.ridesservice.model.dto.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.RideStatus;

public interface RideService {
    RideResponse createRide(RideCreateRequest rideCreateRequest);

    RideResponse getRide(Long id);

    RideResponse setRideStatus(Long id, RideStatus rideStatus);
}
