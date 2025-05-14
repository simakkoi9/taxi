package io.simakkoi9.ridesservice.config.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String userId = attributes.getRequest().getHeader("X-User-Id");
            String userRole = attributes.getRequest().getHeader("X-User-Role");
            if (userId != null) {
                requestTemplate.header("X-User-Id", userId);
            }
            if (userRole != null) {
                requestTemplate.header("X-User-Role", userRole);
            }
        }
    }
} 