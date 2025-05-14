package io.simakkoi9.passengerservice.controller;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PageResponse;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.security.AccessType;
import io.simakkoi9.passengerservice.security.RequiredUserAccess;
import io.simakkoi9.passengerservice.service.PassengerService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/passengers")
@RequiredArgsConstructor
@Validated
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    @RequiredUserAccess(accessType = AccessType.SERVICE_OR_ADMIN_ONLY)
    public PassengerResponse createPassenger(@Validated @RequestBody PassengerCreateRequest passengerCreateRequest) {
        return passengerService.createPassenger(passengerCreateRequest);
    }

    @PatchMapping("/{id}")
    @RequiredUserAccess(accessType = AccessType.USER)
    public PassengerResponse updatePassenger(
            @PathVariable Long id,
            @Validated @RequestBody PassengerUpdateRequest passengerUpdateRequest
    ) {
        return passengerService.updatePassenger(id, passengerUpdateRequest);
    }

    @DeleteMapping("/{id}")
    @RequiredUserAccess(accessType = AccessType.USER)
    public PassengerResponse deletePassenger(@PathVariable Long id) {
        return passengerService.deletePassenger(id);
    }

    @GetMapping("/{id}")
    @RequiredUserAccess(accessType = AccessType.USER)
    public PassengerResponse getPassenger(@PathVariable Long id) {
        return passengerService.getPassenger(id);
    }

    @GetMapping
    @RequiredUserAccess(accessType = AccessType.ADMIN_ONLY)
    public PageResponse<PassengerResponse> getAllPassengers(
            @Min(value = 0, message = "{page.current.min}") @RequestParam(defaultValue = "0")  int page,
            @Min(value = 1, message = "{page.size.min}") @RequestParam(defaultValue = "10") int size
    ) {
        return passengerService.getAllPassengers(page, size);
    }
}
