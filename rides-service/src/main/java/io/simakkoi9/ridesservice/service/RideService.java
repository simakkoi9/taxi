package io.simakkoi9.ridesservice.service;

import io.simakkoi9.ridesservice.model.dto.kafka.KafkaDriverDto;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.response.PageResponse;
import io.simakkoi9.ridesservice.model.dto.rest.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.RideStatus;

public interface RideService {
    RideResponse createRide(RideCreateRequest rideCreateRequest);

    RideResponse updateRide(String id, RideUpdateRequest rideUpdateRequest);

    RideResponse getAvailableDriver(String id);

    void handleAvailableDriver(String rideId, KafkaDriverDto kafkaDriverDto);

    RideResponse changeRideStatus(String id, RideStatus rideStatus);

    RideResponse getRide(String id);

    PageResponse<RideResponse> getAllRides(int page, int size);
}
