package io.simakkoi9.ridesservice.config.feign;

import feign.Capability;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final RequestHeaderInterceptor requestHeaderInterceptor;

    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Capability capability(final MeterRegistry registry) {
        return new MicrometerCapability(registry);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestHeaderInterceptor;
    }
}
