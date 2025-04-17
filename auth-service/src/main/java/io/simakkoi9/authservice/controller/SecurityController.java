package io.simakkoi9.authservice.controller;

import io.simakkoi9.authservice.model.dto.security.request.register.DriverRegisterRequest;
import io.simakkoi9.authservice.model.dto.security.request.LoginRequest;
import io.simakkoi9.authservice.model.dto.security.request.register.PassengerRegisterRequest;
import io.simakkoi9.authservice.model.dto.security.response.DriverResponse;
import io.simakkoi9.authservice.model.dto.security.response.PassengerResponse;
import io.simakkoi9.authservice.model.dto.security.response.TokenResponse;
import io.simakkoi9.authservice.service.impl.SecurityServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityServiceImpl securityService;

    @PostMapping("/passenger/register")
    public Mono<ResponseEntity<PassengerResponse>> registerPassenger(@RequestBody PassengerRegisterRequest request) {
        return securityService.registerPassenger(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/driver/register")
    public Mono<ResponseEntity<DriverResponse>> registerDriver(@RequestBody DriverRegisterRequest request) {
        return securityService.registerDriver(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponse>> login(@RequestBody LoginRequest request) {
        return securityService.login(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<TokenResponse>> refreshToken(@RequestParam String refreshToken) {
        return securityService.refreshToken(refreshToken)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestParam String refreshToken) {
        return securityService.logout(refreshToken)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}
