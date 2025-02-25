package io.simakkoi9.ridesservice.exception;

import org.springframework.context.MessageSource;

public class AvailableDriverProcessingException extends CustomRuntimeException {
    public AvailableDriverProcessingException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
