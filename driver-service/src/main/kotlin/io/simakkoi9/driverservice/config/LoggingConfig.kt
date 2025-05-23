package io.simakkoi9.driverservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter

@Configuration
class LoggingConfig {
    @Bean
    fun logFilter(): CommonsRequestLoggingFilter {
        val filter = CommonsRequestLoggingFilter()
        filter.setIncludeQueryString(true)
        filter.setIncludePayload(false)
        filter.setIncludeHeaders(false)
        filter.setIncludeClientInfo(true)
        filter.setAfterMessagePrefix("Incoming Request: ")
        return filter
    }
}