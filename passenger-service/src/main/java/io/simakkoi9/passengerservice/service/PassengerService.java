package io.simakkoi9.passengerservice.service;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PageResponse;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;

public interface PassengerService {
    PassengerResponse createPassenger(PassengerCreateRequest passengerCreateRequest);

    PassengerResponse updatePassenger(Long id, PassengerUpdateRequest passengerUpdateRequest);

    PassengerResponse deletePassenger(Long id);

    PassengerResponse getPassenger(Long id);

    PageResponse<PassengerResponse> getAllPassengers(int page, int size);

    boolean isPassengerOwner(Long passengerId, String userId);
}
