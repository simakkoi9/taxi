package io.simakkoi9.passengerservice.service.impl;

import io.simakkoi9.passengerservice.exception.DuplicateFoundException;
import io.simakkoi9.passengerservice.exception.ResourceNotFoundException;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.simakkoi9.passengerservice.util.ErrorMessages.DUPLICATE_FOUND_MESSAGE;
import static io.simakkoi9.passengerservice.util.ErrorMessages.RESOURCE_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper mapper;
    private final PassengerRepository repository;

    @Override
    @Transactional
    public PassengerResponse createPassenger(PassengerCreateRequest passengerCreateRequest) {
        if (repository.existsByEmailAndStatus(passengerCreateRequest.email(), UserStatus.ACTIVE)){
            throw new DuplicateFoundException(DUPLICATE_FOUND_MESSAGE.formatted(passengerCreateRequest.email()));
        }
        Passenger passenger = mapper.createRequestToEntity(passengerCreateRequest);
        passenger.setStatus(UserStatus.ACTIVE);
        passenger.setCreatedAt(Timestamp.from(Instant.now()));
        return mapper.toResponse(repository.save(passenger));
    }

    @Override
    @Transactional
    public PassengerResponse updatePassenger(Long id, PassengerUpdateRequest passengerUpdateRequest) {
        Passenger passenger = findActivePassenger(id);
        mapper.setPassengerUpdateRequest(passengerUpdateRequest, passenger);
        return mapper.toResponse(repository.save(passenger));
    }

    @Override
    @Transactional
    public PassengerResponse deletePassenger(Long id) {
        Passenger passenger = findActivePassenger(id);
        passenger.setStatus(UserStatus.DELETED);
        return mapper.toResponse(repository.save(passenger));
    }

    @Override
    public PassengerResponse getPassenger(Long id) {
        return mapper.toResponse(findActivePassenger(id));
    }

    @Override
    public List<PassengerResponse> getAllPassengers() {
        List<Passenger> passengers = new ArrayList<>();
        repository.findAllByStatus(UserStatus.ACTIVE).forEach(passengers::add);
        return passengers.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private Passenger findActivePassenger(Long id){
        return repository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE.formatted("ID " + id)));
    }

}
