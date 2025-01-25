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

import static io.simakkoi9.passengerservice.util.ErrorMessages.*;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper mapper;
    private final PassengerRepository repository;

    @Override
    @Transactional
    public PassengerResponse createPassenger(PassengerCreateRequest passengerCreateRequest) {
        Passenger passenger = mapper.createRequestToEntity(passengerCreateRequest);
        passenger.setStatus(UserStatus.ACTIVE);
        passenger.setCreatedAt(Timestamp.from(Instant.now()));
        if (repository.existsByEmail(passengerCreateRequest.email())){
            throw new DuplicateFoundException(String.format(DUPLICATE_FOUND_MESSAGE, passengerCreateRequest.email()));
        }
        return mapper.toResponse(repository.save(passenger));
    }

    @Override
    @Transactional
    public PassengerResponse updatePassenger(Long id, PassengerUpdateRequest passengerUpdateRequest) {
        Passenger passenger = findPassenger(id);
        mapper.setPassengerUpdateRequest(passengerUpdateRequest, passenger);
        return mapper.toResponse(repository.save(passenger));
    }

    @Override
    @Transactional
    public PassengerResponse deletePassenger(String email) {
        Passenger passenger = findPassenger(email);
        passenger.setStatus(UserStatus.DELETED);
        return mapper.toResponse(repository.save(passenger));
    }

    @Override
    @Transactional
    public PassengerResponse deletePassenger(Long id) {
        Passenger passenger = findPassenger(id);
        passenger.setStatus(UserStatus.DELETED);
        return mapper.toResponse(repository.save(passenger));
    }

    @Override
    public PassengerResponse getPassenger(String email){
        return mapper.toResponse(findPassenger(email));
    }

    @Override
    public PassengerResponse getPassenger(Long id) {
        return mapper.toResponse(findPassenger(id));
    }

    @Override
    public List<PassengerResponse> getAllPassengers() {
        List<Passenger> passengers = new ArrayList<>();
        repository.findAll().forEach(passengers::add);
        return passengers.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }


    private Passenger findPassenger(Long id){
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
    }
    private Passenger findPassenger(String email){
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
    }
}
