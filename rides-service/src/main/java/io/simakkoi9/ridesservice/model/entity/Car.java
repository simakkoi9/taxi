package io.simakkoi9.ridesservice.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Car implements Serializable {
    private Long id;

    private String brand;

    private String model;

    private String color;

    private String number;
}
