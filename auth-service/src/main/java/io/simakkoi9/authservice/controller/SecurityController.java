package io.simakkoi9.authservice.controller;

import io.simakkoi9.authservice.model.dto.security.request.register.DriverRegisterRequest;
import io.simakkoi9.authservice.model.dto.security.request.LoginRequest;
import io.simakkoi9.authservice.model.dto.security.request.register.PassengerRegisterRequest;
import io.simakkoi9.authservice.model.dto.security.response.DriverResponse;
import io.simakkoi9.authservice.model.dto.security.response.PassengerResponse;
import io.simakkoi9.authservice.model.dto.security.response.TokenResponse;
import io.simakkoi9.authservice.service.SecurityService;
import io.simakkoi9.authservice.service.impl.SecurityServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class SecurityController {

    private final SecurityService securityService;

    @PostMapping("/passenger/register")
    public ResponseEntity<PassengerResponse> registerPassenger(
            @RequestBody @Valid PassengerRegisterRequest request
    ) {
        return ResponseEntity.ok(securityService.registerPassenger(request));
    }

    @PostMapping("/driver/register")
    public ResponseEntity<DriverResponse> registerDriver(
            @RequestBody @Valid DriverRegisterRequest request
    ) {
        return ResponseEntity.ok(securityService.registerDriver(request));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponse>> login(@RequestBody @Valid LoginRequest request) {
        return securityService.login(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<TokenResponse>> refreshToken(@RequestParam String refreshToken) {
        return securityService.refreshToken(refreshToken)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
        securityService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }
}
