package io.simakkoi9.ridesservice.model.converter;

import io.simakkoi9.ridesservice.model.entity.Gender;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class GenderWritingConverter implements Converter<Gender, Integer> {
    @Override
    public Integer convert(Gender gender) {
        return gender.getCode();
    }
}
