package io.simakkoi9.ridesservice.service;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface FareService {
    Mono<BigDecimal> calculateFare(String pickupAddress, String destinationAddress);
}
