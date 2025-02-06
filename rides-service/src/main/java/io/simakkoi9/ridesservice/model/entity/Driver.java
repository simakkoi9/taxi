package io.simakkoi9.ridesservice.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Driver implements Serializable {
    private Long id;

    private String name;

    private String email;

    private String phone;

    private Gender gender;

    private Car car;

}
