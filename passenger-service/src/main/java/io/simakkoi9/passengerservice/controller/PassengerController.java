package io.simakkoi9.passengerservice.controller;

import io.simakkoi9.passengerservice.model.dto.request.PassengerCreateRequest;
import io.simakkoi9.passengerservice.model.dto.request.PassengerRequest;
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
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    public PassengerResponse createPassenger(@Validated @RequestBody PassengerCreateRequest passengerCreateRequest){
        return passengerService.createPassenger(passengerCreateRequest);
    }
    @PutMapping("/{id}")
    public PassengerResponse updatePassenger(
            @PathVariable Long id,
            @Validated @RequestBody PassengerUpdateRequest passengerUpdateRequest
    ){
        return passengerService.updatePassenger(id, passengerUpdateRequest);
    }

    @DeleteMapping("/{id}")
    public PassengerResponse deletePassenger(@PathVariable Long id){
        return passengerService.deletePassenger(id);
    }

    @DeleteMapping()
    public PassengerResponse deletePassenger(@Validated @RequestBody PassengerRequest passengerDeleteRequest){
        return passengerService.deletePassenger(passengerDeleteRequest.email());
    }

    @GetMapping("/{id}")
    public PassengerResponse getPassenger(@PathVariable Long id){
        return passengerService.getPassenger(id);
    }

    @GetMapping()
    public PassengerResponse getPassenger(@Validated @RequestBody PassengerRequest passengerGetRequest){
        return passengerService.getPassenger(passengerGetRequest.email());
    }

    @GetMapping("/all")
    public List<PassengerResponse> getAllPassengers(){
        return passengerService.getAllPassengers();
    }
}
