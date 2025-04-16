package io.simakkoi9.authservice.model.dto.security.request.register;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PassengerRegisterRequest {
    private String name;

    private String email;

    private String phone;

    private String password;
} 