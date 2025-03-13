package io.simakkoi9.ridesservice.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.simakkoi9.ridesservice.exception.PassengerServiceNotAvailableException;
import io.simakkoi9.ridesservice.exception.PassengerNotFoundException;
import io.simakkoi9.ridesservice.util.MessageKeyConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassengerErrorDecoder implements ErrorDecoder {

    private final MessageSource messageSource;

    @Override
    public Exception decode(String s, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        if (status == HttpStatus.NOT_FOUND) {
            return new PassengerNotFoundException(
                    MessageKeyConstants.PASSENGER_NOT_FOUND_ERROR,
                    messageSource
            );
        }

        return new PassengerServiceNotAvailableException(MessageKeyConstants.PASSENGER_NOT_AVAILABLE_ERROR, messageSource);
    }

}
