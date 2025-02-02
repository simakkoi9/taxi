package io.simakkoi9.ridesservice.model.converter;

import io.simakkoi9.ridesservice.model.entity.Gender;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class GenderReadingConverter implements Converter<Integer, Gender> {
    @Override
    public Gender convert(Integer code) {
        return Gender.fromCode(code);
    }
}

