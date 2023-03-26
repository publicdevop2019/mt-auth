package com.mt.access.domain.model.audit;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.UserId;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@Slf4j
public class AuditLogAspectConfig {
    public static String AUDIT_PREFIX = "[AUDIT]";

    @Around(value = "@annotation(AuditLog)", argNames = "AuditLog")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        log.info("{} user: {} action: {}", AUDIT_PREFIX, userId, auditLog.actionName());
        return joinPoint.proceed();
    }

}
