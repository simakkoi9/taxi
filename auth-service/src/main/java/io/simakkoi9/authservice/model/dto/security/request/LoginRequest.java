package io.simakkoi9.authservice.model.dto.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "{email.not.blank}")
    @Email(message = "{email.not.valid}")
    @Size(max = 320, message = "{email.max.size}")
    private String email;

    @NotBlank(message = "{pass.not.blank}")
    private String password;
} 