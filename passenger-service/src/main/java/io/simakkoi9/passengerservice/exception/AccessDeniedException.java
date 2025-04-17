package io.simakkoi9.passengerservice.exception;

import org.springframework.context.MessageSource;

public class AccessDeniedException extends CustomRuntimeException {
    
    public AccessDeniedException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }
} 