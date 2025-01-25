package io.simakkoi9.passengerservice.service;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;

import java.util.List;

public interface PassengerService {
    PassengerResponse createPassenger(PassengerCreateRequest passengerCreateRequest);

    PassengerResponse updatePassenger(Long id, PassengerUpdateRequest passengerUpdateRequest);

    PassengerResponse deletePassenger(Long id);

    PassengerResponse getPassenger(Long id);

    List<PassengerResponse> getAllPassengers();
}
