package com.mt.common.domain.model.distributed_lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SagaDistLock {
    String keyExpression();
    String aggregateName();

    int unlockAfter() default 5;
}
