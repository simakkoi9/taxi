package io.simakkoi9.ridesservice.config;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, MILLISECONDS.toMillis(1000), 5);
    }

}
