package io.simakkoi9.authservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE(1),
    FEMALE(2);

    private final int code;

    Gender(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @JsonCreator
    public static Gender fromValue(String value) {
        for (Gender gender : values()) {
            if (gender.name().equalsIgnoreCase(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender value: " + value);
    }

    public static Gender fromCode(int code) {
        for (Gender gender : values()) {
            if (gender.code == code) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender code: " + code);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
} 