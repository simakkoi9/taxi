package io.simakkoi9.passengerservice.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class DuplicatePassengerFoundException extends CustomRuntimeException {
    public DuplicatePassengerFoundException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
