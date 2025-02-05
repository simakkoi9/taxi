package io.simakkoi9.ratingservice.config;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
import java.util.ResourceBundle;

@ApplicationScoped
@Getter
@Setter
public class MessageConfig {

    private Locale locale = Locale.ENGLISH;

    public String getMessage(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return bundle.getString(key);
    }
}
