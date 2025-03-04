package io.simakkoi9.ridesservice.exception;

import org.springframework.context.MessageSource;

public class BusyPassengerException extends CustomRuntimeException {
    public BusyPassengerException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
