package io.simakkoi9.ratingservice.config.message;

import io.simakkoi9.ratingservice.config.locale.LocaleContext;
import io.simakkoi9.ratingservice.exception.InvalidValidationMessageKeyException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.MessageInterpolator;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
import java.util.ResourceBundle;

@ApplicationScoped
@Getter
@Setter
public class MessageConfig implements MessageInterpolator {

    @Inject
    LocaleContext localeContext;

    public String getMessage(String key) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", localeContext.getLocale());
            return bundle.getString(key);
        } catch (InvalidValidationMessageKeyException e) {
            return key;
        }
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return interpolate(messageTemplate, context, localeContext.getLocale());
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        String key = messageTemplate.replace("{", "").replace("}", "");
        return getMessage(key);
    }

}
