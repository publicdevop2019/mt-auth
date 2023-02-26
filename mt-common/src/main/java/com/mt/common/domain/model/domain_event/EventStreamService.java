package com.mt.common.domain.model.domain_event;

import java.util.function.Consumer;
import javax.annotation.Nullable;

public interface EventStreamService {
    //more detailed subscribe logic
    void subscribe(String subscribedApplicationName, boolean internal, @Nullable String queueName,
                   Consumer<StoredEvent> consumer, String... topics);

    //basic subscribe logic
    void subscribe(String exchangeName, String routingKey, String queueName, boolean autoDelete,
                   Consumer<StoredEvent> consumer, String... topics);

    //custom subscribe error handling
    void subscribe(@Nullable String exchangeName,
                   String routingKey,
                   String queueName,
                   boolean autoDelete,
                   Consumer<StoredEvent> consumer,
                   ErrorHandleStrategy strategy,
                   String... topics);

    void next(String appName, boolean internal, String topic, StoredEvent event);

    void next(StoredEvent event);

    enum ErrorHandleStrategy {
        REQUEUE,
        DELAY_1MIN,
        MANUAL;
    }
}
