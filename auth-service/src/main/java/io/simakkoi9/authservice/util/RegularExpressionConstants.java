package io.simakkoi9.authservice.util;

public final class RegularExpressionConstants {
    public static final String PHONE_REGEX = "^\\+?[1-9][0-9]{7,14}$";

    public static final String NAME_REGEX = "^[A-Za-zА-Яа-яЁё\\s\\-']{1,70}$";

    public static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";

    public static final String KEYCLOAK_USER_ID_REGEX = ".*/([^/]+)$";

}
