package io.simakkoi9.ridesservice.exception;

import org.springframework.context.MessageSource;

public class DistanceProcessingException extends CustomRuntimeException {
    public DistanceProcessingException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }

}
