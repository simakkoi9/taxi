package io.simakkoi9.passengerservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageKeyConstants {
    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";

    public static final String PASSENGER_NOT_FOUND = "passenger.not.found";

    public static final String DUPLICATE_PASSENGER_FOUND = "duplicate.passenger.found";
}
