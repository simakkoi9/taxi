package io.simakkoi9.passengerservice.exception;

import org.springframework.context.MessageSource;

public class PassengerNotFoundException extends CustomRuntimeException {
    public PassengerNotFoundException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
