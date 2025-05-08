package io.simakkoi9.authservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageKeyConstants {
    public static final String USER_NOT_FOUND = "user.not.found.error";

    public static final String ROLE_NOT_FOUND = "role.not.found.error";

    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";

    public static final String USER_REGISTRATION_ERROR = "user.registration.error";

    public static final String USER_LOGIN_ERROR = "user.login.error";
}
