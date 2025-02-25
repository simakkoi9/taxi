package io.simakkoi9.passengerservice.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class PassengerNotFoundException extends CustomRuntimeException {
    public PassengerNotFoundException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
