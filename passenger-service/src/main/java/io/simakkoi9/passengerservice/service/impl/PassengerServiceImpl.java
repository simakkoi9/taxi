package io.simakkoi9.passengerservice.service.impl;

import io.simakkoi9.passengerservice.exception.DuplicatePassengerFoundException;
import io.simakkoi9.passengerservice.exception.PassengerNotFoundException;
import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import io.simakkoi9.passengerservice.model.mapper.PassengerMapper;
import io.simakkoi9.passengerservice.repository.PassengerRepository;
import io.simakkoi9.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.simakkoi9.passengerservice.util.ErrorMessages.DUPLICATE_PASSENGER_FOUND_MESSAGE;
import static io.simakkoi9.passengerservice.util.ErrorMessages.PASSENGER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper mapper;
    private final PassengerRepository repository;

    @Override
    @Transactional
    public PassengerResponse createPassenger(PassengerCreateRequest passengerCreateRequest) {
        if (repository.existsByEmailAndStatus(passengerCreateRequest.email(), UserStatus.ACTIVE)){
            throw new DuplicatePassengerFoundException(DUPLICATE_PASSENGER_FOUND_MESSAGE.formatted(passengerCreateRequest.email()));
        }
        Passenger passenger = mapper.createRequestToEntity(passengerCreateRequest);
        Passenger createdPassenger = repository.save(passenger);
        return mapper.toResponse(createdPassenger);
    }

    @Override
    @Transactional
    public PassengerResponse updatePassenger(Long id, PassengerUpdateRequest passengerUpdateRequest) {
        Passenger passenger = findActivePassengerOrElseThrow(id);
        mapper.setPassengerUpdateRequest(passengerUpdateRequest, passenger);
        Passenger updatedPassenger = repository.save(passenger);
        return mapper.toResponse(updatedPassenger);
    }

    @Override
    @Transactional
    public PassengerResponse deletePassenger(Long id) {
        Passenger passenger = findActivePassengerOrElseThrow(id);
        passenger.setStatus(UserStatus.DELETED);
        Passenger deletedPassenger = repository.save(passenger);
        return mapper.toResponse(deletedPassenger);
    }

    @Override
    public PassengerResponse getPassenger(Long id) {
        Passenger passenger = findActivePassengerOrElseThrow(id);
        return mapper.toResponse(passenger);
    }

    @Override
    public List<PassengerResponse> getAllPassengers() {
        List<Passenger> passengers = repository.findAllByStatus(UserStatus.ACTIVE);
        return passengers.stream()
                .map(mapper::toResponse)
                .toList();
    }

    private Passenger findActivePassengerOrElseThrow(Long id){
        return repository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new PassengerNotFoundException(PASSENGER_NOT_FOUND_MESSAGE.formatted("ID " + id)));
    }

}
