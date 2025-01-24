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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper mapper;
    private final PassengerRepository repository;

    @Override
    public PassengerResponse createPassenger(PassengerCreateRequest passengerCreateRequest) {
        Passenger passenger = mapper.createRequestToEntity(passengerCreateRequest);
        if (repository.existsByEmail(passengerCreateRequest.email())){
            throw new DuplicateFoundException();
        }
        return mapper.toResponse(repository.save(passenger));
    }

    @Override
    public PassengerResponse updatePassenger(Long id, PassengerUpdateRequest passengerUpdateRequest) {
        Passenger passenger = findPassenger(id);
        mapper.setPassengerUpdateRequest(passengerUpdateRequest, passenger);
        return mapper.toResponse(repository.save(passenger));
    }

    @Override
    public PassengerResponse deletePassenger(String email) {
        Passenger passenger = findPassenger(email);
        passenger.setStatus(UserStatus.DELETED);
        return mapper.toResponse(repository.save(passenger));
    }

    @Override
    public PassengerResponse deletePassenger(Long id) {
        Passenger passenger = findPassenger(id);
        passenger.setStatus(UserStatus.DELETED);
        return mapper.toResponse(repository.save(passenger));
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
        return repository.findById(id).orElseThrow(
                ResourceNotFoundException::new
        );
    }
    private Passenger findPassenger(String email){
        return repository.findByEmail(email)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
