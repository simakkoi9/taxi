package io.simakkoi9.ridesservice.client;

import io.simakkoi9.ridesservice.exception.PassengerNotAvailableException;
import io.simakkoi9.ridesservice.model.dto.feign.PassengerRequest;
import io.simakkoi9.ridesservice.util.MessageKeyConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassengerClientFallback implements PassengerClient {

    private final MessageSource messageSource;

    @Override
    public PassengerRequest getPassengerById(Long id) {
        throw new PassengerNotAvailableException(MessageKeyConstants.PASSENGER_NOT_AVAILABLE_ERROR, messageSource);
    }
}
