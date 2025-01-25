package com.mt.common.domain.model.domain_event;

import java.util.function.Consumer;

public interface SagaEventStreamService extends EventStreamService {
    <T extends DomainEvent> void of(String eventName, Class<T> clazz,
                                    Consumer<T> consumer);

    <T extends DomainEvent> void replyOf(String eventName, Class<T> clazz,
                                         Consumer<T> consumer);

    <T extends DomainEvent> void replyCancelOf(String eventName, Class<T> clazz,
                                               Consumer<T> consumer);

    <T extends DomainEvent> void cancelOf(String eventName, Class<T> clazz,
                                          Consumer<T> consumer);

}
