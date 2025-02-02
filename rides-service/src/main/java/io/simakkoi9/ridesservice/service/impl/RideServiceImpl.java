package io.simakkoi9.ridesservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.simakkoi9.ridesservice.exception.RideNotFoundException;
import io.simakkoi9.ridesservice.model.dto.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.Car;
import io.simakkoi9.ridesservice.model.entity.Driver;
import io.simakkoi9.ridesservice.model.entity.Passenger;
import io.simakkoi9.ridesservice.model.entity.Ride;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import io.simakkoi9.ridesservice.model.mapper.RideMapper;
import io.simakkoi9.ridesservice.repository.RideRepository;
import io.simakkoi9.ridesservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideMapper mapper;
    private final WebClient osrmWebClient;
    private final RideRepository repository;
    private final static BigDecimal START_FARE = new BigDecimal("3");
    private final static BigDecimal FARE_PER_KM = new BigDecimal("2.5");

    @Override
    public RideResponse createRide(RideCreateRequest rideCreateRequest) {
        Ride ride = mapper.toEntity(rideCreateRequest);
        ride.setPassenger(findPassengerOrElseThrow(rideCreateRequest.passengerId()));
        ride.setDriver(findAvailableDriverOrElseThrow());
        BigDecimal cost = calculateFare(
                rideCreateRequest.pickupAddress(),
                rideCreateRequest.destinationAddress()
        ).block();
        ride.setCost(cost);

        Ride createdRide = repository.save(ride);
        return mapper.toResponse(createdRide);
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

    @Override
    public RideResponse setRideStatus(String id, RideStatus rideStatus) {
        Ride ride = findRideByIdOrElseThrow(id);
        ride.setStatus(rideStatus);
        Ride updatedRide = repository.save(ride);
        return mapper.toResponse(updatedRide);
    }

    private Passenger findPassengerOrElseThrow(Long id){
        return new Passenger();
    }

    private Driver findAvailableDriverOrElseThrow(){
        Driver driver = new Driver();
        driver.setCar(new Car());
        return driver;
    }

    private Ride findRideByIdOrElseThrow(String id){
        return repository.findById(id).orElseThrow(
                () -> new RideNotFoundException("")
        );
    }

    private Mono<Double> getDistance(String pickupAddress, String destinationAddress) {
        String[] pickupSplit = pickupAddress.split("\\s*,\\s*");
        String[] destinationSplit = destinationAddress.split("\\s*,\\s*");

        String requestBody = "%s,%s;%s,%s".formatted(pickupSplit[1], pickupSplit[0], destinationSplit[1], pickupSplit[0]);

        return osrmWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/route/v1/driving")
                        .path("/" + requestBody)
                        .queryParam("overview", "false")
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .handle((response, sink) -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonResponse = objectMapper.readTree(response);
                        double distanceMeters = jsonResponse.get("routes")
                                .get(0)
                                .get("distance")
                                .asDouble();
                        System.out.println("distance: " + distanceMeters);
                        double distanceKm = distanceMeters / 1000.0;
                        sink.next(distanceKm);
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException("Error parsing OSRM response", e));
                    }
                });
    }

    private BigDecimal calculateFareFromDistance(double distanceKm) {
        return FARE_PER_KM.multiply(new BigDecimal(String.valueOf(distanceKm))).add(START_FARE);
    }

    public Mono<BigDecimal> calculateFare(String pickupAddress, String destinationAddress) {
        return getDistance(pickupAddress, destinationAddress)
                .map(this::calculateFareFromDistance);
    }

}
