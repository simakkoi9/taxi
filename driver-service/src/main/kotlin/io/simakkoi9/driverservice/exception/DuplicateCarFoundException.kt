package io.simakkoi9.driverservice.exception

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

class DuplicateCarFoundException(
    messageKey: String,
    messageSource: MessageSource,
    vararg args: Any
) : CustomRuntimeException(messageKey, messageSource, *args)