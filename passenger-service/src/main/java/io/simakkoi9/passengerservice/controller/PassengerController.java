package io.simakkoi9.passengerservice.controller;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PageResponse;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.service.PassengerService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/passengers")
@RequiredArgsConstructor
@Validated
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    public PassengerResponse createPassenger(@Validated @RequestBody PassengerCreateRequest passengerCreateRequest) {
        return passengerService.createPassenger(passengerCreateRequest);
    }

    @PatchMapping("/{id}")
    public PassengerResponse updatePassenger(
            @PathVariable Long id,
            @Validated @RequestBody PassengerUpdateRequest passengerUpdateRequest
    ) {
        return passengerService.updatePassenger(id, passengerUpdateRequest);
    }

    @DeleteMapping("/{id}")
    public PassengerResponse deletePassenger(@PathVariable Long id) {
        return passengerService.deletePassenger(id);
    }

    @GetMapping("/{id}")
    public PassengerResponse getPassenger(@PathVariable Long id) {
        return passengerService.getPassenger(id);
    }

    @GetMapping
    public PageResponse<PassengerResponse> getAllPassengers(
            @Min(value = 0, message = "{page.current.min}") @RequestParam(defaultValue = "0")  int page,
            @Min(value = 1, message = "{page.size.min}") @RequestParam(defaultValue = "10") int size
    ) {
        return passengerService.getAllPassengers(page, size);
    }
}
