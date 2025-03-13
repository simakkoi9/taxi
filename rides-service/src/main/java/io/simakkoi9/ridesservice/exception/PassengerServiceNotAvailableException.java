package io.simakkoi9.ridesservice.exception;

import org.springframework.context.MessageSource;

public class PassengerServiceNotAvailableException extends CustomRuntimeException {
    public PassengerServiceNotAvailableException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
