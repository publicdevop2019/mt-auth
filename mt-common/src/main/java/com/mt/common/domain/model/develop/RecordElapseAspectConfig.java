package com.mt.common.domain.model.develop;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@Slf4j
public class RecordElapseAspectConfig {

    @Pointcut("@annotation(com.mt.common.domain.model.develop.RecordElapseTime)")
    public void recordElapse() {
        //for aop purpose
    }

    @Around(value = "com.mt.common.domain.model.develop.RecordElapseAspectConfig.recordElapse()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = Instant.now().toEpochMilli();
        Object proceed = joinPoint.proceed();
        log.info("elapse time for {} [method] {} is [{}]",
            joinPoint.getSignature().getDeclaringType(), joinPoint.getSignature().getName(),
            Instant.now().toEpochMilli() - startTime);
        return proceed;
    }
}
