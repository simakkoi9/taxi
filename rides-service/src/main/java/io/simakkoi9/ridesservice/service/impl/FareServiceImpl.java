package io.simakkoi9.ridesservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.simakkoi9.ridesservice.exception.DistanceProcessingException;
import io.simakkoi9.ridesservice.service.FareService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FareServiceImpl implements FareService {

    private final WebClient osrmWebClient;
    private final MessageSource messageSource;

    private final BigDecimal START_FARE = new BigDecimal("3");
    private final BigDecimal FARE_PER_KM = new BigDecimal("2.5");

    @Override
    public Mono<BigDecimal> calculateFare(String pickupAddress, String destinationAddress) {
        return getDistance(pickupAddress, destinationAddress)
                .map(this::calculateFareFromDistance);
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
                        sink.error(new DistanceProcessingException("", messageSource, e.getMessage()));
                    }
                });
    }

    private BigDecimal calculateFareFromDistance(double distanceKm) {
        return FARE_PER_KM.multiply(new BigDecimal(String.valueOf(distanceKm))).add(START_FARE);
    }
}
