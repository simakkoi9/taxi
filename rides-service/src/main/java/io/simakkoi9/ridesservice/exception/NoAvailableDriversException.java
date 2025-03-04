package io.simakkoi9.ridesservice.exception;

import org.springframework.context.MessageSource;

public class NoAvailableDriversException extends CustomRuntimeException {

    public NoAvailableDriversException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
