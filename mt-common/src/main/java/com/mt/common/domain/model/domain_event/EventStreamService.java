package com.mt.common.domain.model.domain_event;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface EventStreamService {
    void subscribe(String subscribedApplicationName, boolean internal, @Nullable String queueName, Consumer<StoredEvent> consumer, String... topics);
    void next(String appName, boolean internal, String topic, StoredEvent event);
    void next(StoredEvent event);
}
