package io.simakkoi9.authservice.exception;

import org.springframework.context.MessageSource;

public class LoginFailedException extends CustomRuntimeException {
    public LoginFailedException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }
}
