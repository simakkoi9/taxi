package io.simakkoi9.authservice.model.dto.security.request.register;

import io.simakkoi9.authservice.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverRegisterRequest {
    private String name;

    private String email;

    private String password;

    private String phone;

    private Gender gender;
} 