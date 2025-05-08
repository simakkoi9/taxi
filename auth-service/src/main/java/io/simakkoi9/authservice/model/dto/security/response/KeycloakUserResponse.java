package io.simakkoi9.authservice.model.dto.security.response;

import lombok.Data;

@Data
public class KeycloakUserResponse {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private boolean enabled;
    private boolean emailVerified;
} 