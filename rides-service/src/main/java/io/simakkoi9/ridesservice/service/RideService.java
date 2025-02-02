package io.simakkoi9.ridesservice.service;

import io.simakkoi9.ridesservice.model.dto.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RideService {
    RideResponse createRide(RideCreateRequest rideCreateRequest);

    RideResponse updateRide(String id, RideUpdateRequest rideUpdateRequest);

    RideResponse getAvailableDriver(String id);

    RideResponse changeRideStatus(String id, RideStatus rideStatus);

    RideResponse getRide(String id);

    Page<RideResponse> getAllRides(Pageable pageable);
}
