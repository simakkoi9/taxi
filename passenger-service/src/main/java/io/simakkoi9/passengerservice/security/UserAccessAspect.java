package io.simakkoi9.passengerservice.security;

import io.simakkoi9.passengerservice.exception.AccessDeniedException;
import io.simakkoi9.passengerservice.service.PassengerService;
import io.simakkoi9.passengerservice.util.MessageKeyConstants;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class UserAccessAspect {

    private final PassengerService passengerService;
    private final MessageSource messageSource;

    @Before("@annotation(io.simakkoi9.passengerservice.security.RequiredUserAccess)")
    public void checkUserAccess(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequiredUserAccess annotation = signature.getMethod().getAnnotation(RequiredUserAccess.class);
        AccessType accessType = annotation.accessType();

        String userRole = attributes.getRequest().getHeader("X-User-Role");
        String userId = attributes.getRequest().getHeader("X-User-Id");

        if (UserRole.ROLE_SERVICE.name().equals(userRole) && accessType == AccessType.SERVICE_OR_ADMIN_ONLY) {
            return;
        }
        
        if (userId == null) {
            throw new AccessDeniedException(MessageKeyConstants.SECURITY_NO_USER_ID, messageSource);
        }

        if (userRole == null) {
            throw new AccessDeniedException(MessageKeyConstants.SECURITY_NO_USER_ROLE, messageSource);
        }

        if (accessType == AccessType.ADMIN_ONLY && !UserRole.ROLE_ADMIN.name().equals(userRole)) {
            throw new AccessDeniedException(MessageKeyConstants.SECURITY_ADMIN_ACCESS, messageSource);
        }

        if (UserRole.ROLE_ADMIN.name().equals(userRole) && accessType == AccessType.ADMIN_ONLY) {
            return;
        }

        if (UserRole.ROLE_ADMIN.name().equals(userRole) && accessType == AccessType.SERVICE_OR_ADMIN_ONLY) {
            return;
        }

        if (UserRole.ROLE_ADMIN.name().equals(userRole) && accessType == AccessType.USER) {
            return;
        }

        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Long id) {
            if (!passengerService.isPassengerOwner(id, userId)) {
                throw new AccessDeniedException(MessageKeyConstants.SECURITY_ACCESS_FORBIDDEN, messageSource);
            }
        }
    }
} 