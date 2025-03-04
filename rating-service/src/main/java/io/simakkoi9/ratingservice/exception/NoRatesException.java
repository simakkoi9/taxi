package io.simakkoi9.ratingservice.exception;

import io.simakkoi9.ratingservice.config.message.MessageConfig;

public class NoRatesException extends CustomRuntimeException {

    public NoRatesException(String messageKey, MessageConfig messageConfig, Object... args) {
        super(messageKey, messageConfig, args);
    }


}
