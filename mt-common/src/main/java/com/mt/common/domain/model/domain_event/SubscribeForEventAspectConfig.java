package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.model.clazz.ClassUtility;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@Slf4j
public class SubscribeForEventAspectConfig {
    @Autowired
    private EventRepository eventRepository;

    @Pointcut("@annotation(com.mt.common.domain.model.domain_event.SubscribeForEvent)")
    public void listen() {
        //for aop purpose
    }

    @Around(value = "com.mt.common.domain.model.domain_event.SubscribeForEventAspectConfig.listen()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        log.trace("subscribe for event change for {}",jp.getSignature().toShortString());
        DomainEventPublisher
                .instance()
                .subscribe(new DomainEventSubscriber<DomainEvent>() {
                    public void handleEvent(DomainEvent event) {
                        if(log.isTraceEnabled()){
                        log.trace("appending {}",event.getName());
                        }else{
                        log.debug("appending {}", ClassUtility.getShortName(event.getName()));
                        }
                        eventRepository.append(event);
                    }

                    public Class<DomainEvent> subscribedToEventType() {
                        return DomainEvent.class; // all domain events
                    }
                });
        Object proceed = jp.proceed();
        log.trace("unsubscribe for event change for{}",jp.getSignature().toShortString());
        DomainEventPublisher.instance().reset();
        return proceed;
    }
}
