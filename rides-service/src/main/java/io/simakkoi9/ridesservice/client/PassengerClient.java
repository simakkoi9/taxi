package io.simakkoi9.ridesservice.client;

import io.simakkoi9.ridesservice.model.entity.Passenger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passenger-client", url = "http://localhost:8080/api/v1/passengers")
public interface PassengerClient {

    @GetMapping("/{id}")
    Passenger getPassengerById(@PathVariable Long id);

}
