package io.simakkoi9.ridesservice.model.entity;

import lombok.Data;

@Data
public class Car {
    private Long id;

    private String brand;

    private String model;

    private String color;

    private String number;
}
