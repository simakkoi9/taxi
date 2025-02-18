package io.simakkoi9.ridesservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegularExpressionsConstants {
    public static final String ADDRESS_REGEX = "^-?\\d{1,3}(\\.\\d{1,6})?,\\s?-?\\d{1,3}(\\.\\d{1,6})?$";
}
