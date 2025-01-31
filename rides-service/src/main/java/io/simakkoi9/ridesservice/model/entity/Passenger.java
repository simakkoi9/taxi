package io.simakkoi9.ridesservice.model.entity;

import lombok.Data;

@Data
public class Passenger {
    private Long id;

    private String name;

    private String email;

    private String phone;
}
