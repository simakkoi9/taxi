package io.simakkoi9.ratingservice.config.locale;

import jakarta.enterprise.context.RequestScoped;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
@RequestScoped
public class LocaleContext {

    private Locale locale = Locale.of("ru");

}
