package io.simakkoi9.passengerservice.controller.impl;

import io.simakkoi9.passengerservice.controller.PassengerController;
import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerUpdateRequest;
import io.simakkoi9.passengerservice.model.dto.response.PassengerResponse;
import io.simakkoi9.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/passengers")
@RequiredArgsConstructor
public class PassengerControllerImpl implements PassengerController {

    private final PassengerService passengerService;

    @Override
    @PostMapping
    public PassengerResponse createPassenger(@Validated @RequestBody PassengerCreateRequest passengerCreateRequest){
        return passengerService.createPassenger(passengerCreateRequest);
    }

    @Override
    @PutMapping("/{id}")
    public PassengerResponse updatePassenger(
            @PathVariable Long id,
            @Validated @RequestBody PassengerUpdateRequest passengerUpdateRequest
    ){
        return passengerService.updatePassenger(id, passengerUpdateRequest);
    }

    @Override
    @DeleteMapping("/{id}")
    public PassengerResponse deletePassenger(@PathVariable Long id){
        return passengerService.deletePassenger(id);
    }

    @Override
    @GetMapping("/{id}")
    public PassengerResponse getPassenger(@PathVariable Long id){
        return passengerService.getPassenger(id);
    }

    @Override
    @GetMapping()
    public List<PassengerResponse> getAllPassengers(){
        return passengerService.getAllPassengers();
    }
}
