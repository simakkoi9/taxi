package io.simakkoi9.authservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.simakkoi9.authservice.model.SecurityRole;
import io.simakkoi9.authservice.model.dto.client.DriverCreateRequest;
import io.simakkoi9.authservice.model.dto.client.PassengerCreateRequest;
import io.simakkoi9.authservice.model.dto.security.request.register.DriverRegisterRequest;
import io.simakkoi9.authservice.model.dto.security.request.KeycloakUserRequest;
import io.simakkoi9.authservice.model.dto.security.request.LoginRequest;
import io.simakkoi9.authservice.model.dto.security.request.register.PassengerRegisterRequest;
import io.simakkoi9.authservice.model.dto.security.response.DriverResponse;
import io.simakkoi9.authservice.model.dto.security.response.PassengerResponse;
import io.simakkoi9.authservice.model.dto.security.response.TokenResponse;
import io.simakkoi9.authservice.model.dto.security.response.KeycloakUserResponse;
import io.simakkoi9.authservice.model.dto.security.response.KeycloakRoleResponse;
import io.simakkoi9.authservice.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final WebClient passengerServiceClient;
    private final WebClient driverServiceClient;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${service.passenger.url}")
    private String passengerServiceUrl;

    @Value("${service.driver.url}")
    private String driverServiceUrl;

    private Mono<String> getAdminToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        return webClient.post()
                .uri(keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::getAccessToken);
    }

    private Mono<String> getUserId(String adminToken, String email) {
        return webClient.get()
                .uri(keycloakServerUrl + "/admin/realms/" + realm + "/users?email=" + email)
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(List.class)
                .handle((users, sink) -> {
                    if (users.isEmpty()) {
                        sink.error(new RuntimeException("User not found"));
                        return;
                    }
                    Map<String, Object> userMap = (Map<String, Object>) users.get(0);
                    sink.next(objectMapper.convertValue(userMap, KeycloakUserResponse.class).getId());
                });
    }

    private Mono<KeycloakRoleResponse> assignRole(String adminToken, String userId, String roleName) {
        return webClient.get()
                .uri(keycloakServerUrl + "/admin/realms/" + realm + "/roles")
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(List.class)
                .<KeycloakRoleResponse>handle((roles, sink) -> {
                    for (Object role : roles) {
                        Map<String, Object> roleMap = (Map<String, Object>) role;
                        if (roleName.equals(roleMap.get("name"))) {
                            sink.next(objectMapper.convertValue(roleMap, KeycloakRoleResponse.class));
                            return;
                        }
                    }
                    sink.error(new RuntimeException("Role not found: " + roleName));
                })
                .flatMap(role -> {
                    List<KeycloakRoleResponse> roleMappings = Collections.singletonList(role);
                    return webClient.post()
                            .uri(keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm")
                            .header("Authorization", "Bearer " + adminToken)
                            .header("Content-Type", "application/json")
                            .bodyValue(roleMappings)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .thenReturn(role);
                });
    }

    private Mono<PassengerResponse> sendPassengerData(String keycloakId, PassengerRegisterRequest request) {
        PassengerCreateRequest passengerData = new PassengerCreateRequest(
                keycloakId,
                request.getName(),
                request.getEmail(),
                request.getPhone()
        );

        return passengerServiceClient.post()
                .uri(passengerServiceUrl)
                .header("Content-Type", "application/json")
                .header("X-User-Role", SecurityRole.ROLE_SERVICE.name())
                .bodyValue(passengerData)
                .retrieve()
                .bodyToMono(PassengerResponse.class);
    }

    private Mono<DriverResponse> sendDriverData(String keycloakId, DriverRegisterRequest request) {
        DriverCreateRequest driverData = new DriverCreateRequest(
                keycloakId,
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getGender()
        );

        return driverServiceClient.post()
                .uri(driverServiceUrl)
                .header("Content-Type", "application/json")
                .header("X-User-Role", SecurityRole.ROLE_SERVICE.name())
                .bodyValue(driverData)
                .retrieve()
                .bodyToMono(DriverResponse.class);
    }

    public Mono<PassengerResponse> registerPassenger(PassengerRegisterRequest request) {
        return getAdminToken().flatMap(adminToken -> {
            KeycloakUserRequest userRequest = KeycloakUserRequest.builder()
                    .username(request.getName())
                    .email(request.getEmail())
                    .enabled(true)
                    .emailVerified(false)
                    .firstName(request.getName())
                    .credentials(Collections.singletonList(
                            KeycloakUserRequest.Credential.builder()
                                    .type("password")
                                    .value(request.getPassword())
                                    .temporary(false)
                                    .build()
                    ))
                    .build();

            return webClient.post()
                    .uri(keycloakServerUrl + "/admin/realms/" + realm + "/users")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + adminToken)
                    .bodyValue(userRequest)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .then(getUserId(adminToken, request.getEmail()))
                    .flatMap(userId -> assignRole(adminToken, userId, SecurityRole.ROLE_PASSENGER.name())
                            .then(sendPassengerData(userId, request))
                    );
        });
    }

    public Mono<DriverResponse> registerDriver(DriverRegisterRequest request) {
        return getAdminToken().flatMap(adminToken -> {
            KeycloakUserRequest userRequest = KeycloakUserRequest.builder()
                    .username(request.getName())
                    .email(request.getEmail())
                    .enabled(true)
                    .emailVerified(false)
                    .firstName(request.getName())
                    .credentials(Collections.singletonList(
                            KeycloakUserRequest.Credential.builder()
                                    .type("password")
                                    .value(request.getPassword())
                                    .temporary(false)
                                    .build()
                    ))
                    .build();

            return webClient.post()
                    .uri(keycloakServerUrl + "/admin/realms/" + realm + "/users")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + adminToken)
                    .bodyValue(userRequest)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .then(getUserId(adminToken, request.getEmail()))
                    .flatMap(userId -> assignRole(adminToken, userId, SecurityRole.ROLE_DRIVER.name())
                            .then(sendDriverData(userId, request))
                    );
        });
    }

    public Mono<TokenResponse> login(LoginRequest request) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", request.getEmail());
        formData.add("password", request.getPassword());

        return webClient.post()
                .uri(keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    public Mono<TokenResponse> refreshToken(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        return webClient.post()
                .uri(keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    public Mono<Void> logout(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        return webClient.post()
                .uri(keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
