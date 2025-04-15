package io.simakkoi9.authservice.model.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeycloakTokenRequest {
    @Builder.Default
    private String grantType = "password";
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String refreshToken;
} 