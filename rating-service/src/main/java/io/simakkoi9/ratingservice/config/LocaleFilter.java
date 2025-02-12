package io.simakkoi9.ratingservice.config;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.Locale;

@Provider
@Priority(Priorities.USER)
@RequestScoped
public class LocaleFilter implements ContainerRequestFilter {

    @Inject
    MessageConfig messageConfig;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        List<Locale> locales = requestContext.getAcceptableLanguages();
        Locale locale = locales.isEmpty() ? Locale.ENGLISH : locales.get(0);
        messageConfig.setLocale(locale);
    }
}
