package io.simakkoi9.ridesservice.service.impl;

import io.micrometer.core.annotation.Timed;
import io.simakkoi9.ridesservice.client.PassengerClient;
import io.simakkoi9.ridesservice.exception.AvailableDriverProcessingException;
import io.simakkoi9.ridesservice.exception.BusyPassengerException;
import io.simakkoi9.ridesservice.exception.InvalidStatusException;
import io.simakkoi9.ridesservice.exception.NoAvailableDriversException;
import io.simakkoi9.ridesservice.exception.RideNotFoundException;
import io.simakkoi9.ridesservice.model.dto.feign.PassengerRequest;
import io.simakkoi9.ridesservice.model.dto.kafka.KafkaDriverRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.response.PageResponse;
import io.simakkoi9.ridesservice.model.dto.rest.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.Driver;
import io.simakkoi9.ridesservice.model.entity.Passenger;
import io.simakkoi9.ridesservice.model.entity.Ride;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import io.simakkoi9.ridesservice.model.mapper.DriverMapper;
import io.simakkoi9.ridesservice.model.mapper.PassengerMapper;
import io.simakkoi9.ridesservice.model.mapper.RideMapper;
import io.simakkoi9.ridesservice.repository.RideRepository;
import io.simakkoi9.ridesservice.service.FareService;
import io.simakkoi9.ridesservice.service.RideService;
import io.simakkoi9.ridesservice.service.kafka.KafkaProducer;
import io.simakkoi9.ridesservice.util.MessageKeyConstants;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideMapper mapper;
    private final PassengerMapper passengerMapper;
    private final DriverMapper driverMapper;
    private final RideRepository repository;
    private final FareService fareService;
    private final MessageSource messageSource;
    private final PassengerClient passengerClient;
    private final KafkaProducer kafkaProducer;
    private final ConcurrentHashMap<String, BlockingQueue<Optional<KafkaDriverRequest>>> responseCache =
            new ConcurrentHashMap<>();

    @Override
    @Transactional
    @Timed(value = "ride_service_create_ride_timer", description = "Timer for create ride method")
    public RideResponse createRide(RideCreateRequest rideCreateRequest) {
        Ride ride = mapper.toEntity(rideCreateRequest);

        Passenger passenger = findFreePassengerOrElseThrow(rideCreateRequest.passengerId());
        ride.setPassenger(passenger);

        BigDecimal cost = fareService.calculateFare(
            rideCreateRequest.pickupAddress(),
            rideCreateRequest.destinationAddress()
        ).block();
        ride.setCost(cost);

        Ride createdRide = repository.save(ride);
        return mapper.toResponse(createdRide);
    }

    @Override
    @Transactional
    public RideResponse updateRide(String id, RideUpdateRequest rideUpdateRequest) {
        Ride ride = findRideByIdOrElseThrow(id);

        mapper.partialUpdate(rideUpdateRequest, ride);

        if (rideUpdateRequest.pickupAddress() != null || rideUpdateRequest.destinationAddress() != null) {
            BigDecimal cost = fareService.calculateFare(
                    ride.getPickupAddress(), ride.getDestinationAddress()
            ).block();
            ride.setCost(cost);
        }

        Ride updatedRide = repository.save(ride);
        return mapper.toResponse(updatedRide);
    }

    @Override
    @Transactional
    @Timed(value = "ride_service_get_available_driver_timer", description = "Timer for get available driver method")
    public RideResponse getAvailableDriver(String id) {
        final Ride ride = findRideByIdOrElseThrow(id);
        findAvailableDriver(id);

        BlockingQueue<Optional<KafkaDriverRequest>> queue =
                responseCache.computeIfAbsent(id, k -> new LinkedBlockingQueue<>());
        Optional<KafkaDriverRequest> kafkaDriverRequest = Optional.empty();
        try {
            Optional<KafkaDriverRequest> result = queue.poll(5, TimeUnit.SECONDS);
            if (Objects.isNull(result)) {
                throw new AvailableDriverProcessingException(
                        MessageKeyConstants.DRIVER_PROCESSING_ERROR,
                        messageSource
                );
            }
            kafkaDriverRequest = Optional.of(result).flatMap(Function.identity());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (kafkaDriverRequest.isEmpty()) {
            throw new NoAvailableDriversException(MessageKeyConstants.NO_DRIVERS_ERROR, messageSource);
        }

        Driver driver = driverMapper.toEntity(kafkaDriverRequest.get());
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ACCEPTED);
        Ride updatedRide = repository.save(ride);
        responseCache.remove(id);

        return mapper.toResponse(updatedRide);
    }

    @Override
    public void handleAvailableDriver(String rideId, KafkaDriverRequest kafkaDriverRequest) {
        BlockingQueue<Optional<KafkaDriverRequest>> queue = responseCache.get(rideId);
        if (queue != null) {
            Optional<KafkaDriverRequest> driverRequest = Optional.ofNullable(kafkaDriverRequest);
            boolean isOffered = queue.offer(driverRequest);
        }
    }

    @Override
    @Transactional
    public RideResponse changeRideStatus(String id, RideStatus rideStatus) {
        Ride ride = findRideByIdOrElseThrow(id);
        if (
            ride.getStatus().getCode() >= rideStatus.getCode()
                || (
                    rideStatus.getCode() > ride.getStatus().getCode() + 1
                    && !RideStatus.getImmutableStatusList().contains(rideStatus)
                )
                || RideStatus.getImmutableStatusList().contains(ride.getStatus())
        ) {
            throw new InvalidStatusException(MessageKeyConstants.INVALID_STATUS, messageSource, rideStatus.toValue());
        }

        ride.setStatus(rideStatus);
        Ride updatedRide = repository.save(ride);
        return mapper.toResponse(updatedRide);
    }

    @Override
    public RideResponse getRide(String id) {
        Ride ride = findRideByIdOrElseThrow(id);
        return mapper.toResponse(ride);
    }

    @Override
    public PageResponse<RideResponse> getAllRides(int page, int size) {
        Page<Ride> rides = repository.findAll(PageRequest.of(page, size));
        return mapper.toPageResponse(rides);
    }

    @Override
    public String getRidePersonId(String rideId, String person) {
        String personId = null;
        if (repository.findById(rideId).isPresent()) {
            Ride ride = repository.findById(rideId).get();
            if (person.equals("driver")) {
                personId = "driver_" + ride.getDriver().getId();
            }
            if (person.equals("passenger")) {
                personId = "passenger_" + ride.getPassenger().getId();
            }
        }

        return personId;
    }

    private Ride findRideByIdOrElseThrow(String id) {
        return repository.findById(id).orElseThrow(
                () -> new RideNotFoundException(MessageKeyConstants.RIDE_NOT_FOUND_ERROR, messageSource, id)
        );
    }

    private Passenger findFreePassengerOrElseThrow(Long id) {
        if (repository.existsByPassenger_IdAndStatusIn(id, RideStatus.getBusyPassengerStatusList())) {
            throw new BusyPassengerException(MessageKeyConstants.BUSY_PASSENGER_ERROR, messageSource, id);
        }
        PassengerRequest passengerRequest = passengerClient.getPassengerById(id);

        return passengerMapper.toEntity(passengerRequest);
    }

    private void findAvailableDriver(String rideId) {
        List<Long> driverIdList = repository.findAllByStatusIn(RideStatus.getBusyDriverStatusList())
                .stream()
                .map(Ride::getDriver)
                .map(Driver::getId)
                .toList();

        kafkaProducer.sendDriverIdList(rideId, driverIdList);
    }
}
