package io.simakkoi9.ridesservice.client;

import io.simakkoi9.ridesservice.exception.PassengerNotFoundException;
import io.simakkoi9.ridesservice.util.MessageKeyConstants;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
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

        return errorDecoder.decode(s, response);
    }

}
