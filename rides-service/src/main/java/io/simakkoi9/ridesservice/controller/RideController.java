package io.simakkoi9.ridesservice.controller;

import io.simakkoi9.ridesservice.model.dto.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import io.simakkoi9.ridesservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping
    public RideResponse createRide(@Validated @RequestBody RideCreateRequest rideCreateRequest){
        return rideService.createRide(rideCreateRequest);
    }

    @GetMapping("/{id}")
    public RideResponse getRide(@PathVariable Long id){
        return rideService.getRide(id);
    }

    @PatchMapping("/{id}")
    public RideResponse setRideStatus(@PathVariable Long id, @RequestParam RideStatus status){
        return rideService.setRideStatus(id, status);
    }
}
