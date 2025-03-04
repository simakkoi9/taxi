package io.simakkoi9.ridesservice.exception;

import org.springframework.context.MessageSource;

public class PassengerNotAvailableException extends CustomRuntimeException {
    public PassengerNotAvailableException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
