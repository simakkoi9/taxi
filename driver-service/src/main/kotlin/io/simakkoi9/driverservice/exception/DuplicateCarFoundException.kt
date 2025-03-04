package io.simakkoi9.driverservice.exception

import org.springframework.context.MessageSource

class DuplicateCarFoundException(
    messageKey: String,
    messageSource: MessageSource,
    vararg args: Any
) : CustomRuntimeException(messageKey, messageSource, *args)