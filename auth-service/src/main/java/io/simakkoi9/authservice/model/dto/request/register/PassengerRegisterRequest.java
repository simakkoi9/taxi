package io.simakkoi9.authservice.model.dto.request.register;

import lombok.Data;

@Data
public class PassengerRegisterRequest {
    private String name;

    private String email;

    private String phone;

    private String password;
} 