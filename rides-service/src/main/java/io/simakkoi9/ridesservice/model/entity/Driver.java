package io.simakkoi9.ridesservice.model.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class Driver implements Serializable {
    private Long id;

    private String name;

    private String email;

    private String phone;

    private Gender gender;

    private Car car;

}
