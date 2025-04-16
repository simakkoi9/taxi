package io.simakkoi9.authservice.model.dto.security.request;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeycloakUserRequest {
    private String username;
    private String email;
    private String firstName;
    private boolean enabled;
    private boolean emailVerified;
    private List<Credential> credentials;

    @Data
    @Builder
    public static class Credential {
        private String type;
        private String value;
        private boolean temporary;
    }
} 