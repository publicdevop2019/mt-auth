package com.mt.common.domain.model.domain_event;

import java.util.function.Consumer;

public interface SagaEventStreamService extends EventStreamService{
    void replyOf(String subscribedApplicationName, boolean internal, String eventName, Consumer<StoredEvent> consumer);
    void replyCancelOf(String subscribedApplicationName, boolean internal, String eventName, Consumer<StoredEvent> consumer);
    void cancelOf(String subscribedApplicationName, boolean internal, String eventName, Consumer<StoredEvent> consumer);
    void of(String subscribedApplicationName, boolean internal, String eventName, Consumer<StoredEvent> consumer);
}
