package io.simakkoi9.driverservice.security

import io.simakkoi9.driverservice.exception.AccessDeniedException
import io.simakkoi9.driverservice.service.DriverService
import io.simakkoi9.driverservice.util.MessageKeyConstants
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class UserAccessAspect(
    private val driverService: DriverService,
    private val messageSource: MessageSource
) {

    @Before("@annotation(io.simakkoi9.driverservice.security.RequiredUserAccess)")
    fun checkUserAccess(joinPoint: JoinPoint) {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes

        val signature = joinPoint.signature as MethodSignature
        val annotation = signature.method.getAnnotation(RequiredUserAccess::class.java)
        val accessType = annotation.accessType

        val userRole = attributes.request.getHeader("X-User-Role")
        val userId = attributes.request.getHeader("X-User-Id")

        if (userRole == UserRole.ROLE_SERVICE.name && accessType == AccessType.SERVICE_OR_ADMIN_ONLY) {
            return
        }

        if (userId == null) {
            throw AccessDeniedException(MessageKeyConstants.SECURITY_NO_USER_ID, messageSource)
        }

        if (userRole == null) {
            throw AccessDeniedException(MessageKeyConstants.SECURITY_NO_USER_ROLE, messageSource)
        }

        if (accessType == AccessType.ADMIN_ONLY && userRole != UserRole.ROLE_ADMIN.name) {
            throw AccessDeniedException(MessageKeyConstants.SECURITY_ADMIN_ACCESS, messageSource)
        }

        if (userRole == UserRole.ROLE_ADMIN.name && (
            accessType == AccessType.ADMIN_ONLY ||
            accessType == AccessType.SERVICE_OR_ADMIN_ONLY ||
            accessType == AccessType.USER
        )) {
            return
        }

        val args = joinPoint.args
        if (args.isNotEmpty() && args[0] is Long) {
            val id = args[0] as Long
            if (!driverService.isDriverOwner(id, userId)) {
                throw AccessDeniedException(MessageKeyConstants.SECURITY_ACCESS_FORBIDDEN, messageSource)
            }
        }
    }
} 