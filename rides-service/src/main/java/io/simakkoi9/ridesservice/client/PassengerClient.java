package io.simakkoi9.ridesservice.client;

import io.simakkoi9.ridesservice.config.FeignConfig;
import io.simakkoi9.ridesservice.model.dto.feign.PassengerRequest;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passenger-client", configuration = FeignConfig.class)
public interface PassengerClient {

    @CircuitBreaker(name = "passengerCircuitBreaker", fallbackMethod = "fallback")
    @Retry(name = "passengerServiceRetry", fallbackMethod = "fallback")
    @GetMapping("/{id}")
    PassengerRequest getPassengerById(@PathVariable Long id);

    default PassengerRequest fallback(CallNotPermittedException e) {
        throw e;
    }
}
