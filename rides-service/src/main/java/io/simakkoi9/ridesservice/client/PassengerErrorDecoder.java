package io.simakkoi9.ridesservice.client;

import io.simakkoi9.ridesservice.exception.PassengerNotFoundException;
import io.simakkoi9.ridesservice.exception.PassengerAccessDeniedException;
import io.simakkoi9.ridesservice.util.MessageKeyConstants;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassengerErrorDecoder implements ErrorDecoder {

    private final MessageSource messageSource;

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        if (status == HttpStatus.NOT_FOUND) {
            return new PassengerNotFoundException(
                    MessageKeyConstants.PASSENGER_NOT_FOUND_ERROR,
                    messageSource
            );
        }

        if (status == HttpStatus.FORBIDDEN) {
            return new PassengerAccessDeniedException(
                    MessageKeyConstants.PASSENGER_ACCESS_DENIED_ERROR,
                    messageSource
            );
        }

        return errorDecoder.decode(s, response);
    }

}
