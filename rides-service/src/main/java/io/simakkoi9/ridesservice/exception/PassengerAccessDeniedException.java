package io.simakkoi9.ridesservice.exception;

import org.springframework.context.MessageSource;

public class PassengerAccessDeniedException extends CustomRuntimeException {
    public PassengerAccessDeniedException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }
} 