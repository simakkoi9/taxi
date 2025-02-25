package io.simakkoi9.driverservice.exception

import org.springframework.context.MessageSource

class DuplicateDriverFoundException(
    messageKey: String,
    messageSource: MessageSource,
    vararg args: Any
) : CustomRuntimeException(messageKey, messageSource, *args)