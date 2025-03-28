package io.simakkoi9.ridesservice.controller;

import io.simakkoi9.ridesservice.model.dto.rest.request.RideCreateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.request.RideUpdateRequest;
import io.simakkoi9.ridesservice.model.dto.rest.response.PageResponse;
import io.simakkoi9.ridesservice.model.dto.rest.response.RideResponse;
import io.simakkoi9.ridesservice.model.entity.RideStatus;
import io.simakkoi9.ridesservice.service.RideService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
@Validated
public class RideController {

    private final RideService rideService;

    @PostMapping
    public RideResponse createRide(@Validated @RequestBody RideCreateRequest rideCreateRequest) {
        return rideService.createRide(rideCreateRequest);
    }

    @PutMapping("/{id}")
    public RideResponse updateRide(
            @PathVariable String id,
            @Validated @RequestBody RideUpdateRequest rideUpdateRequest) {
        return rideService.updateRide(id, rideUpdateRequest);
    }

    @PatchMapping("/{id}/getDriver")
    public RideResponse getAvailableDriver(@PathVariable String id) {
        return rideService.getAvailableDriver(id);
    }

    @PatchMapping("/{id}")
    public RideResponse changeRideStatus(@PathVariable String id, @RequestParam RideStatus status) {
        return rideService.changeRideStatus(id, status);
    }

    @GetMapping("/{id}")
    public RideResponse getRide(@PathVariable String id) {
        return rideService.getRide(id);
    }

    @GetMapping
    public PageResponse<RideResponse> getAllRides(
            @Min(value = 0, message = "{page.current.min}") @RequestParam(defaultValue = "0") int page,
            @Min(value = 1, message = "{page.size.min}") @RequestParam(defaultValue = "10") int size
    ) {
        return rideService.getAllRides(page, size);
    }
}
