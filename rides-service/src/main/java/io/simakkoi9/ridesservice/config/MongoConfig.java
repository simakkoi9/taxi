package io.simakkoi9.ridesservice.config;

import io.simakkoi9.ridesservice.model.converter.GenderReadingConverter;
import io.simakkoi9.ridesservice.model.converter.GenderWritingConverter;
import io.simakkoi9.ridesservice.model.converter.RideStatusReadingConverter;
import io.simakkoi9.ridesservice.model.converter.RideStatusWritingConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoConfig {
    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(
                new GenderReadingConverter(),
                new GenderWritingConverter(),
                new RideStatusReadingConverter(),
                new RideStatusWritingConverter()
            )
        );
    }
}
