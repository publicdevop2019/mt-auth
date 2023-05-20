package com.mt.common.domain.model.domain_event;

import java.util.function.Consumer;

public interface SagaEventStreamService extends EventStreamService {
    <T extends DomainEvent> void of(String subscribedApplicationName, boolean internal,
                                    String eventName, Class<T> clazz,
                                    Consumer<T> consumer);

    <T extends DomainEvent> void replyOf(String subscribedApplicationName, boolean internal,
                                         String eventName, Class<T> clazz,
                                         Consumer<T> consumer);

    <T extends DomainEvent> void replyCancelOf(String subscribedApplicationName, boolean internal,
                                               String eventName, Class<T> clazz,
                                               Consumer<T> consumer);

    <T extends DomainEvent> void cancelOf(String subscribedApplicationName, boolean internal,
                                          String eventName, Class<T> clazz,
                                          Consumer<T> consumer);

}
