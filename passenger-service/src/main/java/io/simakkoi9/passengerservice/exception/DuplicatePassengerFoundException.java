package io.simakkoi9.passengerservice.exception;

import org.springframework.context.MessageSource;

public class DuplicatePassengerFoundException extends CustomRuntimeException {
    public DuplicatePassengerFoundException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
