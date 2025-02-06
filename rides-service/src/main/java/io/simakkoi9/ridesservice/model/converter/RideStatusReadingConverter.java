package io.simakkoi9.ridesservice.model.converter;

import io.simakkoi9.ridesservice.model.entity.RideStatus;
import org.springframework.core.convert.converter.Converter;

public class RideStatusReadingConverter implements Converter<Integer, RideStatus> {
    @Override
    public RideStatus convert(Integer code) {
        return RideStatus.fromCode(code);
    }
}
