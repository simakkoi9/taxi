package io.simakkoi9.ridesservice.service.impl;

import io.simakkoi9.ridesservice.exception.BusyPassengerException;
import io.simakkoi9.ridesservice.exception.RideNotFoundException;
import io.simakkoi9.ridesservice.model.dto.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.Car;
import io.simakkoi9.ridesservice.model.entity.Driver;
import io.simakkoi9.ridesservice.model.entity.Passenger;
import io.simakkoi9.ridesservice.model.entity.Ride;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import io.simakkoi9.ridesservice.model.mapper.RideMapper;
import io.simakkoi9.ridesservice.repository.RideRepository;
import io.simakkoi9.ridesservice.service.FareService;
import io.simakkoi9.ridesservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideMapper mapper;
    private final RideRepository repository;
    private final FareService fareService;

    @Override
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
    public RideResponse updateRide(String id, RideUpdateRequest rideUpdateRequest){
        Ride ride = findRideByIdOrElseThrow(id);
        mapper.partialUpdate(rideUpdateRequest, ride);
        Ride updatedRide = repository.save(ride);
        return mapper.toResponse(updatedRide);
    }

    @Override
    public RideResponse getAvailableDriver(String id){
        Ride ride = findRideByIdOrElseThrow(id);
        Driver driver = findAvailableDriverOrElseThrow();

        ride.setDriver(driver);
        ride.setStatus(RideStatus.ACCEPTED);

        return mapper.toResponse(repository.save(ride));
    }

    @Override
    public RideResponse changeRideStatus(String id, RideStatus rideStatus) {
        Ride ride = findRideByIdOrElseThrow(id);
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
    public Page<RideResponse> getAllRides(Pageable pageable) {
        Page<Ride> rides = repository.findAll(pageable);
        List<Ride> rideList = rides.getContent();
        List<RideResponse> rideResponseList = mapper.toResponseList(rideList);
        return new PageImpl<>(rideResponseList, pageable, rides.getTotalElements());
    }

    private Ride findRideByIdOrElseThrow(String id){
        return repository.findById(id).orElseThrow(
                () -> new RideNotFoundException("")
        );
    }

    private Passenger findFreePassengerOrElseThrow(Long id){
        if (repository.existsByPassenger_IdAndStatusIn(id, RideStatus.getBusyPassengerStatusList())){
            throw new BusyPassengerException("");
        }

        return new Passenger();                         //Получим из сервиса пассажиров
    }

//    Из сервиса водителей нужно получить список водителей с машиной
    private Driver findAvailableDriverOrElseThrow(){
        List<Long> driverIdList = repository.findAllByStatusIn(RideStatus.getBusyDriverStatusList())
                .stream()
                .map(Ride::getDriver)
                .map(Driver::getId)
                .toList();
        Driver driver = new Driver();
        driver.setCar(new Car());
        return driver;
    }
}
