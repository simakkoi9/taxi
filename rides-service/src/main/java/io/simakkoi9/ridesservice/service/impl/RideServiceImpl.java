package io.simakkoi9.ridesservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideMapper mapper;
    private final WebClient openRouteWebClient;
    private final RideRepository repository;
    private final static BigDecimal START_FARE = new BigDecimal(5);
    private final static BigDecimal FARE_PER_KM = new BigDecimal("3.4");
    private final static String API_KEY = "5b3ce3597851110001cf6248600bc8a9a1d5403fa618a185fd113d68";

    @Override
    public RideResponse createRide(RideCreateRequest rideCreateRequest) {
        Ride ride = mapper.toEntity(rideCreateRequest);
        ride.setPassenger(getPassenger());
        ride.setDriver(findAvailableDriver());
        BigDecimal cost = calculateFare(
                rideCreateRequest.pickupAddress(),
                rideCreateRequest.destinationAddress()
        ).block();
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
    public RideResponse setRideStatus(Long id, RideStatus rideStatus) {
        Ride ride = repository.findById(id).orElseThrow();
        ride.setStatus(rideStatus);
        Ride updatedRide = repository.save(ride);
        return mapper.toResponse(updatedRide);
    }

    private Passenger getPassenger(){
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
        return Mono.zip(
                    geocodeAddress(pickupAddress),
                    geocodeAddress(destinationAddress)
                ).flatMap(coords -> {
                    String pickupCoords = coords.getT1();
                    String destinationCoords = coords.getT2();

                    String requestBody = String.format("{\"coordinates\": [[%s], [%s]]}", pickupCoords, destinationCoords);

                    return openRouteWebClient.post()
                            .uri("/v2/directions/driving-car/json")
                            .bodyValue(requestBody)
                            .header("Authorization", "Bearer 5b3ce3597851110001cf6248600bc8a9a1d5403fa618a185fd113d68")
                            .retrieve()
                            .bodyToMono(String.class)
                            .handle((response, sink) -> {
                                try {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    JsonNode jsonResponse = objectMapper.readTree(response);
                                    double distanceMeters = jsonResponse.get("routes")
                                            .get(0)
                                            .get("segments")
                                            .get(0)
                                            .get("distance")
                                            .asDouble();

                                    double distanceKm = distanceMeters / 1000;
                                    BigDecimal fare = FARE_PER_KM.multiply(new BigDecimal(distanceKm)).add(START_FARE);
                                    sink.next(fare);
                                } catch (Exception e) {
                                    sink.error(new RuntimeException("", e));
                                }
                            });
                });
    }

    private Mono<String> geocodeAddress(String address) {
        return openRouteWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geocode/search")
                        .queryParam("api_key", API_KEY)
                        .queryParam("text", address.replaceAll(" ", "%20"))
                        .build()
                ).retrieve()
                .bodyToMono(String.class)
                .handle((response, sink) -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonResponse = objectMapper.readTree(response);
                        sink.next(jsonResponse.get("features")
                                .get(0)
                                .get("geometry")
                                .get("coordinates")
                                .toString());
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException(e));
                    }
                });
    }

}
