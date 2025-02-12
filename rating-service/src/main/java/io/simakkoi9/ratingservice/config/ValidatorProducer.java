package io.simakkoi9.ratingservice.config;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.Produces;

@RequestScoped
public class ValidatorProducer {

    @Inject
    MessageConfig messageConfig;

    @Produces
    public Validator createValidator() {
        try (ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(messageConfig)
                .buildValidatorFactory()) {
            return factory.getValidator();
        }
    }

}
