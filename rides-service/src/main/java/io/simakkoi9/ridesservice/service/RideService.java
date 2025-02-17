package io.simakkoi9.ridesservice.service;

import io.simakkoi9.ridesservice.model.dto.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.response.PageResponse;
import io.simakkoi9.ridesservice.model.dto.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.RideStatus;

public interface RideService {
    RideResponse createRide(RideCreateRequest rideCreateRequest);

    RideResponse updateRide(String id, RideUpdateRequest rideUpdateRequest);

    RideResponse getAvailableDriver(String id);

    RideResponse changeRideStatus(String id, RideStatus rideStatus);

    RideResponse getRide(String id);

    PageResponse<RideResponse> getAllRides(int page, int size);
}
