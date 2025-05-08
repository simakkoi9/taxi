package io.simakkoi9.authservice.exception;

import org.springframework.context.MessageSource;

public class KeycloakUserRegistrationFailedException extends CustomRuntimeException{
    public KeycloakUserRegistrationFailedException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }
}
