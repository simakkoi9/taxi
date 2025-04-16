package io.simakkoi9.authservice.service;

import io.simakkoi9.authservice.model.dto.security.request.LoginRequest;
import io.simakkoi9.authservice.model.dto.security.request.register.DriverRegisterRequest;
import io.simakkoi9.authservice.model.dto.security.request.register.PassengerRegisterRequest;
import io.simakkoi9.authservice.model.dto.security.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface SecurityService {
    Mono<TokenResponse> registerPassenger(PassengerRegisterRequest request);

    Mono<TokenResponse> registerDriver(DriverRegisterRequest request);

    Mono<TokenResponse> login(LoginRequest request);

    Mono<TokenResponse> refreshToken(String refreshToken);

    Mono<Void> logout(String refreshToken);
}
