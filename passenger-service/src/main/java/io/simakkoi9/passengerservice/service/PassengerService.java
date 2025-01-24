package io.simakkoi9.passengerservice.service;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.model.entity.Passenger;

import java.util.List;

public interface PassengerService {
    PassengerResponse createPassenger(PassengerCreateRequest passengerCreateRequest);

    PassengerResponse updatePassenger(Long id, PassengerUpdateRequest passengerUpdateRequest);

    PassengerResponse deletePassenger(String email);

    PassengerResponse deletePassenger(Long id);

    PassengerResponse getPassenger(Long id);

    PassengerResponse getPassenger(String email);

    List<PassengerResponse> getAllPassengers();
}
