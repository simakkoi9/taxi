package io.simakkoi9.authservice.exception;

import org.springframework.context.MessageSource;

public class RoleNotFoundException extends CustomRuntimeException{
    public RoleNotFoundException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }
}
