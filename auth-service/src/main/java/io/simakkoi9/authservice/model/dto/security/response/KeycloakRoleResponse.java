package io.simakkoi9.authservice.model.dto.security.response;

import lombok.Data;

@Data
public class KeycloakRoleResponse {
    private String id;
    private String name;
    private String description;
    private boolean composite;
    private boolean clientRole;
    private String containerId;
} 