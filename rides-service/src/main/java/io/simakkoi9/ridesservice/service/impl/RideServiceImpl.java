package io.simakkoi9.ridesservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.simakkoi9.ridesservice.model.dto.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.response.RideResponse;
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
    private final static BigDecimal START_FARE = new BigDecimal("5");
    private final static BigDecimal FARE_PER_KM = new BigDecimal("3.4");

    @Override
    public RideResponse createRide(RideCreateRequest rideCreateRequest) {
        Ride ride = mapper.toEntity(rideCreateRequest);
        ride.setPassenger(getPassenger(rideCreateRequest.passengerId()));
        ride.setDriver(findAvailableDriver());
        BigDecimal cost = calculateFare(
                rideCreateRequest.pickupAddress(),
                rideCreateRequest.destinationAddress()
        ).block();
        System.out.println(cost);
        ride.setCost(cost);
        Ride createdRide = repository.save(ride);
        return mapper.toResponse(createdRide);
    }

    @Override
    public RideResponse getRide(Long id) {
        Ride ride = findRideByIdOrElseThrow(id);
        return mapper.toResponse(ride);
    }

    @Override
    public Page<RideResponse> getAllRides(Pageable pageable) {
        Page<Ride> rides = repository.getAll(pageable);
        List<Ride> ridesList = rides.toList();
        List<RideResponse> rideResponseList = mapper.toResponseList(ridesList);
        return new PageImpl<>(rideResponseList, pageable, rides.getTotalElements());
    }

    @Override
    public RideResponse setRideStatus(Long id, RideStatus rideStatus) {
        Ride ride = repository.findById(id).orElseThrow();
        ride.setStatus(rideStatus);
        Ride updatedRide = repository.save(ride);
        return mapper.toResponse(updatedRide);
    }

    private Passenger getPassenger(Long id){
        return new Passenger();
    }

    private Driver findAvailableDriver(){
        return new Driver();
    }

    private Ride findRideByIdOrElseThrow(Long id){
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException("")
        );
    }

    private Mono<BigDecimal> calculateFare(String pickupAddress, String destinationAddress) {
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
                        Double distanceKm = distanceMeters / 1000.0;
                        BigDecimal fare = FARE_PER_KM.multiply(new BigDecimal(String.valueOf(distanceKm))).add(START_FARE);
                        sink.next(fare);
                    } catch (Exception e) {
                        sink.error(new RuntimeException("", e));
                    }
                });
    }
}
