package io.simakkoi9.passengerservice.service.impl;

import io.simakkoi9.passengerservice.exception.DuplicatePassengerFoundException;
import io.simakkoi9.passengerservice.exception.PassengerNotFoundException;
import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PageResponse;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.model.entity.Passenger;
import io.simakkoi9.passengerservice.model.entity.UserStatus;
import io.simakkoi9.passengerservice.model.mapper.PassengerMapper;
import io.simakkoi9.passengerservice.repository.PassengerRepository;
import io.simakkoi9.passengerservice.service.PassengerService;
import io.simakkoi9.passengerservice.util.MessageKeyConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper mapper;
    private final PassengerRepository repository;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public PassengerResponse createPassenger(PassengerCreateRequest passengerCreateRequest) {
        String passengerEmail = passengerCreateRequest.email();
        if (repository.existsByEmailAndStatus(passengerEmail, UserStatus.ACTIVE)) {
            throw new DuplicatePassengerFoundException(
                    MessageKeyConstants.DUPLICATE_PASSENGER_FOUND,
                    messageSource,
                    passengerEmail
            );
        }
        Passenger passenger = mapper.toEntity(passengerCreateRequest);
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
    public PageResponse<PassengerResponse> getAllPassengers(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        Page<Passenger> passengers = repository.findAllByStatus(UserStatus.ACTIVE, PageRequest.of(page, size));
        return mapper.toPageResponse(passengers);
    }

    private Passenger findActivePassengerOrElseThrow(Long id) {
        return repository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(
                    () -> new PassengerNotFoundException(
                        MessageKeyConstants.PASSENGER_NOT_FOUND,
                        messageSource,
                        id
                    )
                );
    }

}
