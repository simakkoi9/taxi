package io.simakkoi9.ridesservice.service;

import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface FareService {
    Mono<BigDecimal> calculateFare(String pickupAddress, String destinationAddress);
}
