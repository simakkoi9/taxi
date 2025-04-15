package io.simakkoi9.authservice.model.dto.request.register;

import io.simakkoi9.authservice.model.Gender;
import lombok.Data;

@Data
public class DriverRegisterRequest {
    private String name;

    private String email;

    private String password;

    private String phone;

    private Gender gender;
} 