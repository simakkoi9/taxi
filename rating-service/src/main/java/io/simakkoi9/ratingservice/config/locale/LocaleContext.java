package io.simakkoi9.ratingservice.config.locale;

import jakarta.enterprise.context.RequestScoped;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RequestScoped
public class LocaleContext {

    private Locale locale = Locale.of("ru");

}
