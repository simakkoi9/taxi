package io.simakkoi9.ratingservice.exception;

import io.simakkoi9.ratingservice.config.message.MessageConfig;

public class DuplicateRatingException extends CustomRuntimeException {

    public DuplicateRatingException(String messageKey, MessageConfig messageConfig, Object... args) {
        super(messageKey, messageConfig, args);
    }

}
