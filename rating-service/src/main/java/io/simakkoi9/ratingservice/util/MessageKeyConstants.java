package io.simakkoi9.ratingservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageKeyConstants {
    public static final String RATING_NOT_FOUND = "rating.not.found";
    public static final String DUPLICATE_RATING = "duplicate.rating";
    public static final String DRIVER_NO_RATES = "driver.no.rates";
    public static final String PASSENGER_NO_RATES = "passenger.no.rates";
    public static final String DRIVER_ALREADY_RATED = "driver.already.rated";
    public static final String PASSENGER_ALREADY_RATED = "passenger.already.rated";
    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";
}
