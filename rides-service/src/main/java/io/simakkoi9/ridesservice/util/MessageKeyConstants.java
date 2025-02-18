package io.simakkoi9.ridesservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageKeyConstants {
    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";

    public static final String DISTANCE_PROCESSING_ERROR = "distance-processing.error";

    public static final String RIDE_NOT_FOUND_ERROR = "ride.not-found.error";

    public static final String NO_DRIVERS_ERROR = "no.drivers.error";

    public static final String BUSY_PASSENGER_ERROR = "busy.passenger.error";

    public static final String INVALID_STATUS = "invalid.status";
}
