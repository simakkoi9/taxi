package io.simakkoi9.ratingservice.config.locale;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.Locale;

@Provider
@ApplicationScoped
public class LocaleFilter implements ContainerRequestFilter {

    @Inject
    LocaleContext localeContext;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        List<Locale> locales = requestContext.getAcceptableLanguages();
        Locale locale = locales.isEmpty() ? Locale.of("ru") : locales.get(0);
        localeContext.setLocale(locale);
    }
}
