package com.mt.common.domain.model.distributed_lock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Configuration
@Aspect
@Slf4j
@ConditionalOnProperty(
        value="mt.distributed_lock",
        havingValue = "true",
        matchIfMissing = true)
public class SagaDistLockAspectConfig {
    private static final Integer LOCK_WAIT_TIME = 5;

    private final RedissonClient redissonClient;

    public SagaDistLockAspectConfig(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around(value = "@annotation(SagaDistLock)",argNames = "SagaDistLock")
    public Object around(ProceedingJoinPoint joinPoint, SagaDistLock SagaDistLock) throws Throwable {
        String lockKeyValue = extractKey(joinPoint, SagaDistLock);
        String key = lockKeyValue.replace("_cancel","") +"_dist_lock";
        Object obj;
        RLock lock = redissonClient.getLock(key+"_"+SagaDistLock.aggregateName());
        lock.lock(SagaDistLock.unlockAfter(), TimeUnit.SECONDS);
        log.trace("acquire lock success for {}", key);
        obj = joinPoint.proceed();
                //below will not work properly
//            try {
//                obj = joinPoint.proceed();
//            } finally {
//                log.info("releases the lock {}", key);
//                lock.unlock();
//            }
        return obj;
    }

    private Method getTargetMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method agentMethod = methodSignature.getMethod();
        return pjp.getTarget().getClass().getMethod(agentMethod.getName(),agentMethod.getParameterTypes());
    }
    private String extractKey(ProceedingJoinPoint joinPoint, SagaDistLock lockConfig) throws NoSuchMethodException {
        String lockParam = lockConfig.keyExpression();
        Method targetMethod = getTargetMethod(joinPoint);
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new MethodBasedEvaluationContext(new Object(), targetMethod, joinPoint.getArgs(),
                new DefaultParameterNameDiscoverer());
        Expression expression = parser.parseExpression(lockParam);
        return expression.getValue(context, String.class);
    }
}
