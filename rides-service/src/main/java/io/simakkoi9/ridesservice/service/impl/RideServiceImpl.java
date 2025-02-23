package io.simakkoi9.ridesservice.service.impl;

import io.simakkoi9.ridesservice.client.PassengerClient;
import io.simakkoi9.ridesservice.exception.BusyPassengerException;
import io.simakkoi9.ridesservice.exception.InvalidStatusException;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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
    private final ConcurrentHashMap<String, BlockingQueue<KafkaDriverRequest>> responseCache =
            new ConcurrentHashMap<>();

    @Override
    @Transactional
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
        if (
            ride.getStatus().getCode() >= rideUpdateRequest.status().getCode()
                || RideStatus.getImmutableStatusList().contains(ride.getStatus())
        ) {
            throw new InvalidStatusException(
                    MessageKeyConstants.INVALID_STATUS,
                    messageSource,
                    rideUpdateRequest.status().toValue()
            );
        }

        mapper.partialUpdate(rideUpdateRequest, ride);
        Ride updatedRide = repository.save(ride);
        return mapper.toResponse(updatedRide);
    }

    @Override
    @Transactional
    public RideResponse getAvailableDriver(String id) {
        final Ride ride = findRideByIdOrElseThrow(id);
        findAvailableDriver(id);

        BlockingQueue<KafkaDriverRequest> queue = responseCache.computeIfAbsent(id, k -> new LinkedBlockingQueue<>());
        KafkaDriverRequest kafkaDriverRequest = null;
        try {
            kafkaDriverRequest = queue.poll(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (kafkaDriverRequest == null) {
            throw new RuntimeException();
        }

        Driver driver = driverMapper.toEntity(kafkaDriverRequest);
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ACCEPTED);
        Ride updatedRide = repository.save(ride);

        return mapper.toResponse(updatedRide);
    }

    @Override
    public void handleAvailableDriver(String rideId, KafkaDriverRequest kafkaDriverRequest) throws RuntimeException {
        BlockingQueue<KafkaDriverRequest> queue = responseCache.get(rideId);
        if (queue != null) {
            boolean isOffered = queue.offer(kafkaDriverRequest);
        }
    }

    @Override
    @Transactional
    public RideResponse changeRideStatus(String id, RideStatus rideStatus) {
        Ride ride = findRideByIdOrElseThrow(id);
        if (
            ride.getStatus().getCode() >= rideStatus.getCode()
                || (
                    ride.getStatus().getCode() == rideStatus.getCode() - 1
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
