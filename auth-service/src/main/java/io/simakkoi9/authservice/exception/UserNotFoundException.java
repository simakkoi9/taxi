package io.simakkoi9.authservice.exception;

import org.springframework.context.MessageSource;

public class UserNotFoundException extends CustomRuntimeException{
    public UserNotFoundException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }
}
