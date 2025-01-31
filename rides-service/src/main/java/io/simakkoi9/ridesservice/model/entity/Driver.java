package io.simakkoi9.ridesservice.model.entity;

import lombok.Data;

@Data
public class Driver {
    private Long id;

    private String name;

    private String email;

    private String phone;

    private Gender gender;

    private Car car;

}
