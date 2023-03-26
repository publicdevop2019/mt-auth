package com.mt.common.domain.model.domain_event;

import java.util.function.Consumer;
import javax.annotation.Nullable;

public interface EventStreamService {

    /**
     * basic event stream with deserialize stored event
     *
     * @param exchangeName exchange
     * @param routingKey   routing key
     * @param queueName    queue name
     * @param autoDelete   auto delete queue
     * @param consumer     consumer function
     * @param strategy     reject handling strategy
     * @param topics       subscribe topics
     * @param clazz        domain event java class
     * @param <T>          domain event type
     */
    <T extends DomainEvent> void subscribe(String exchangeName,
                                           String routingKey,
                                           String queueName,
                                           boolean autoDelete,
                                           ErrorHandleStrategy strategy,
                                           Consumer<T> consumer,
                                           Class<T> clazz,
                                           String... topics);

    /**
     * basic event stream without deserialize stored event
     *
     * @param exchangeName exchange
     * @param routingKey   routing key
     * @param queueName    queue name
     * @param autoDelete   auto delete queue
     * @param consumer     consumer function
     * @param strategy     reject handling strategy
     * @param topics       subscribe topics
     */
    void subscribe(String exchangeName,
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
