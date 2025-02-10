package io.simakkoi9.ratingservice.exception;

import io.simakkoi9.ratingservice.config.MessageConfig;

public class NoRatesException extends RuntimeException {
    public NoRatesException(String messageKey, MessageConfig messageConfig, Object... args) {
        super(getLocalizedMessage(messageKey, messageConfig, args));
    }
    private static String getLocalizedMessage(String messageKey, MessageConfig messageConfig, Object... args) {
        return messageConfig.getMessage(messageKey).formatted(args);
    }
}
