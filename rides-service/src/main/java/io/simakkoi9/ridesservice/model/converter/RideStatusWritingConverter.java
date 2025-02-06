package io.simakkoi9.ridesservice.model.converter;

import io.simakkoi9.ridesservice.model.entity.RideStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class RideStatusWritingConverter implements Converter<RideStatus, Integer> {
    @Override
    public Integer convert(RideStatus rideStatus) {
        return rideStatus.getCode();
    }
}
