package io.simakkoi9.ridesservice.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class RideNotFoundException extends CustomRuntimeException {
    public RideNotFoundException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
