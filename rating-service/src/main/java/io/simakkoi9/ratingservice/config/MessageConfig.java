package io.simakkoi9.ratingservice.config;

import io.simakkoi9.ratingservice.exception.InvalidValidationMessageKeyException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.MessageInterpolator;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
import java.util.ResourceBundle;

@ApplicationScoped
@Getter
@Setter
public class MessageConfig implements MessageInterpolator {

    private Locale locale = Locale.ENGLISH;

    public String getMessage(String key) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            return bundle.getString(key);
        } catch (InvalidValidationMessageKeyException e) {
            return key;
        }
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return interpolate(messageTemplate, context, locale);
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        String key = messageTemplate.replace("{", "").replace("}", "");
        return getMessage(key);
    }
}
