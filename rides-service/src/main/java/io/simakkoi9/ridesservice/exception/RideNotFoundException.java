package io.simakkoi9.ridesservice.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class RideNotFoundException extends RuntimeException{
    public RideNotFoundException(String messageKey, MessageSource messageSource, Object... args){
        super(getLocalizedMessage(messageKey, messageSource, args));
    }

    private static String getLocalizedMessage(String messageKey, MessageSource messageSource, Object... args) {
        String[] stringArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            stringArgs[i] = args[i].toString();
        }
        return messageSource.getMessage(messageKey, stringArgs, LocaleContextHolder.getLocale());
    }
}
