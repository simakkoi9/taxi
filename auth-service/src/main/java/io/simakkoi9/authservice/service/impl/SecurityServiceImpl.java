package io.simakkoi9.authservice.service.impl;

import io.simakkoi9.authservice.exception.KeycloakUserRegistrationFailedException;
import io.simakkoi9.authservice.exception.LoginFailedException;
import io.simakkoi9.authservice.model.SecurityRole;
import io.simakkoi9.authservice.model.dto.client.request.DriverCreateRequest;
import io.simakkoi9.authservice.model.dto.client.request.PassengerCreateRequest;
import io.simakkoi9.authservice.model.dto.security.request.LoginRequest;
import io.simakkoi9.authservice.model.dto.security.request.register.DriverRegisterRequest;
import io.simakkoi9.authservice.model.dto.security.request.register.PassengerRegisterRequest;
import io.simakkoi9.authservice.model.dto.security.response.DriverResponse;
import io.simakkoi9.authservice.model.dto.security.response.PassengerResponse;
import io.simakkoi9.authservice.model.dto.security.response.TokenResponse;
import io.simakkoi9.authservice.service.SecurityService;
import io.simakkoi9.authservice.util.MessageKeyConstants;
import io.simakkoi9.authservice.util.RegularExpressionConstants;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final Keycloak keycloak;
    private final WebClient webClient;
    private final WebClient passengerServiceClient;
    private final WebClient driverServiceClient;
    private final MessageSource messageSource;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${service.passenger.url}")
    private String passengerServiceUrl;

    @Value("${service.driver.url}")
    private String driverServiceUrl;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private PassengerResponse sendPassengerData(String keycloakId, PassengerRegisterRequest request) {
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
                .bodyToMono(PassengerResponse.class)
                .block();
    }

    private DriverResponse sendDriverData(String keycloakId, DriverRegisterRequest request) {
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
                .bodyToMono(DriverResponse.class)
                .block();
    }

    @Override
    public PassengerResponse registerPassenger(PassengerRegisterRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setFirstName(request.getName());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() != HttpStatus.CREATED.value()) {
            throw new KeycloakUserRegistrationFailedException(
                    MessageKeyConstants.USER_REGISTRATION_ERROR,
                    messageSource
            );
        }

        String userId = response.getLocation().getPath()
                .replaceAll(RegularExpressionConstants.KEYCLOAK_USER_ID_REGEX, "$1");

        RoleRepresentation passengerRole = keycloak.realm(realm).roles()
                .get(SecurityRole.ROLE_PASSENGER.name())
                .toRepresentation();

        keycloak.realm(realm).users().get(userId).roles().realmLevel()
                .add(Collections.singletonList(passengerRole));

        return sendPassengerData(userId, request);
    }

    @Override
    public DriverResponse registerDriver(DriverRegisterRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setFirstName(request.getName());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() != HttpStatus.CREATED.value()) {
            throw new KeycloakUserRegistrationFailedException(
                    MessageKeyConstants.USER_REGISTRATION_ERROR,
                    messageSource
            );
        }

        String userId = response.getLocation().getPath()
                .replaceAll(RegularExpressionConstants.KEYCLOAK_USER_ID_REGEX, "$1");

        RoleRepresentation driverRole = keycloak.realm(realm).roles()
                .get(SecurityRole.ROLE_DRIVER.name())
                .toRepresentation();

        keycloak.realm(realm).users().get(userId).roles().realmLevel()
                .add(Collections.singletonList(driverRole));

        return sendDriverData(userId, request);
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
                .onStatus(
                        httpStatusCode -> httpStatusCode.value() == HttpStatus.UNAUTHORIZED.value(),
                        clientResponse -> Mono.error(
                                new LoginFailedException(MessageKeyConstants.USER_LOGIN_ERROR, messageSource)
                        )
                )
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

    @Override
    public void logout(String refreshToken) {
        keycloak.tokenManager().invalidate(refreshToken);
    }
}
