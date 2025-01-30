package io.simakkoi9.driverservice.exception

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

class DriverNotFoundException(
    messageKey: String,
    messageSource: MessageSource,
    vararg args: Any
) : RuntimeException(getLocalizedMessage(messageKey, messageSource, *args)) {
    companion object {
        private fun getLocalizedMessage(messageKey: String, messageSource: MessageSource, vararg args: Any): String {
            val stringArgs = args.map { it.toString() }.toTypedArray()
            return messageSource.getMessage(messageKey, stringArgs, LocaleContextHolder.getLocale())
        }
    }
}