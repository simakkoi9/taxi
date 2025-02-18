package io.simakkoi9.ridesservice.client;

import feign.Response;
import feign.codec.ErrorDecoder;

public class PassengerErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        return new RuntimeException(String.valueOf(response.status()));
    }

}
