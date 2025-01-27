package io.simakkoi9.passengerservice.model.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE(1),
    DELETED(2);

    private final int code;

    public int getCode() {
        return code;
    }

    public static UserStatus fromCode(int code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }
}
