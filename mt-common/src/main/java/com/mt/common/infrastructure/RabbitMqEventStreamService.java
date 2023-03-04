package com.mt.common.infrastructure;

import static com.mt.common.CommonConstant.EXCHANGE_NAME;
import static com.mt.common.CommonConstant.EXCHANGE_NAME_ALT;
import static com.mt.common.CommonConstant.EXCHANGE_NAME_DELAY;
import static com.mt.common.CommonConstant.EXCHANGE_NAME_REJECT;
import static com.mt.common.CommonConstant.QUEUE_NAME_ALT;
import static com.mt.common.CommonConstant.QUEUE_NAME_DELAY;
import static com.mt.common.CommonConstant.QUEUE_NAME_REJECT;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.clazz.ClassUtility;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MqHelper;
import com.mt.common.domain.model.domain_event.SagaEventStreamService;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMqEventStreamService implements SagaEventStreamService {

    public static final Map<Thread, Channel> pubChannel = new HashMap<>();
    public static final Map<Thread, Channel> subChannel = new HashMap<>();
    private final boolean autoAck = false;
    private final Connection connectionPub;
    private final Connection connectionSub;
    ConcurrentNavigableMap<Long, StoredEvent> outstandingConfirms = new ConcurrentSkipListMap<>();
    @Value("${spring.application.name}")
    private String appName;
    @Resource
    private Environment env;

    public RabbitMqEventStreamService(@Value("${mt.url.support.mq}") final String url) {
        log.debug("initializing event stream service with url {}", url);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(url);
        try {
            connectionSub = factory.newConnection();
        } catch (IOException | TimeoutException ex) {
            throw new DefinedRuntimeException("unable to create subscribe connection", "0000",
                HttpResponseCode.NOT_HTTP,
                ExceptionCatalog.OPERATION_ERROR, ex);
        }
        try {
            connectionPub = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            log.error("unable to create publish connection", e);
            try {
                connectionSub.close();
            } catch (IOException ex) {
                throw new DefinedRuntimeException("error during close subscribe connection", "0001",
                    HttpResponseCode.NOT_HTTP,
                    ExceptionCatalog.OPERATION_ERROR, ex);
            }
            throw new DefinedRuntimeException("unable to create publish connection", "0002",
                HttpResponseCode.NOT_HTTP,
                ExceptionCatalog.OPERATION_ERROR, e);
        }
        log.debug("event stream service initialize success");
    }

    /**
     * release resource
     */
    public void releaseResource() {
        log.info("closing event stream resource");
        try {
            connectionSub.close();
        } catch (IOException ex) {
            log.error("error during close subscribe connection", ex);
        }
        try {
            connectionPub.close();
        } catch (IOException ex) {
            log.error("error during close publish connection", ex);
        }
        subChannel.values().forEach(e -> {
            try {
                e.close();
            } catch (TimeoutException | IOException ex) {
                log.error("error during close subscribe channel", ex);
            }
        });
        pubChannel.values().forEach(e -> {
            try {
                e.close();
            } catch (TimeoutException | IOException ex) {
                log.error("error during close subscribe channel", ex);
            }
        });
    }


    public <T extends DomainEvent> void listen(
        String appName,
        boolean internal,
        @Nullable String queueName,
        Class<T> clazz,
        Consumer<T> consumer,
        String... eventNames) {
        String routingKeyPrefix =
            appName + "." + (internal ? "internal" : "external") + ".";
        boolean autoDelete = false;
        if (queueName == null) {
            //auto delete random generated queue
            autoDelete = true;
            long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
            String s;
            if (eventNames.length == 1) {
                s = MqHelper.handlerOf(appName, eventNames[0]);
            } else {
                s = MqHelper.handlerOf(appName, "combined_events");
            }
            queueName = Long.toString(id, 36) + "_" + s;

        }
        subscribe(null, routingKeyPrefix, queueName, autoDelete, ErrorHandleStrategy.MANUAL,
            consumer, clazz,
            eventNames);
    }

    @Override
    public <T extends DomainEvent> void subscribe(String exchangeName,
                                                  String routingKey,
                                                  String queueName,
                                                  boolean autoDelete,
                                                  ErrorHandleStrategy strategy,
                                                  Consumer<T> consumer,
                                                  Class<T> clazz,
                                                  String... topics) {
        subscribeImpl(exchangeName, routingKey, queueName, autoDelete, strategy, null, consumer,
            clazz,
            topics);
    }

    @Override
    public void subscribe(String exchangeName,
                          String routingKey,
                          String queueName,
                          boolean autoDelete,
                          Consumer<StoredEvent> consumer,
                          ErrorHandleStrategy strategy,
                          String... topics) {
        subscribeImpl(exchangeName, routingKey, queueName, autoDelete, strategy, consumer,
            null, null,
            topics);
    }

    private <T extends DomainEvent> void subscribeImpl(
        String exchangeName,
        String routingKeyPrefix,
        String queueName,
        boolean autoDelete,
        ErrorHandleStrategy strategy,
        Consumer<StoredEvent> consumer1,
        Consumer<T> consumer2,
        Class<T> clazz,
        String... topics) {

        Thread thread = Thread.currentThread();
        Channel channel = subChannel.get(thread);
        if (channel == null) {
            try {
                channel = connectionSub.createChannel();
            } catch (IOException e) {
                throw new DefinedRuntimeException(
                    "unable create subscribe channel with routing key " + routingKeyPrefix +
                        " and queue name " + queueName, "0003",
                    HttpResponseCode.NOT_HTTP,
                    ExceptionCatalog.OPERATION_ERROR, e);
            }
            subChannel.put(thread, channel);
        }
        try {
            HashSet<String> strings = new HashSet<>();
            strings.add(QUEUE_NAME_ALT);
            strings.add(QUEUE_NAME_REJECT);
            strings.add(QUEUE_NAME_DELAY);
            if (strings.contains(queueName)) {
                channel.queueDeclare(queueName, true, false, autoDelete, null);
            } else {
                Map<String, Object> args = new HashMap<>();
                if (strategy.equals(ErrorHandleStrategy.MANUAL)) {
                    args.put("x-dead-letter-exchange", EXCHANGE_NAME_REJECT);
                } else if (strategy.equals(ErrorHandleStrategy.DELAY_1MIN)) {
                    args.put("x-dead-letter-exchange", EXCHANGE_NAME_DELAY);
                }
                channel.queueDeclare(queueName, true, false, autoDelete, args);
            }
            checkExchange(channel);
            //@todo find out proper prefetch value, this requires test in prod env
            channel.basicQos(10);
            for (String topic : topics) {
                channel.queueBind(queueName, exchangeName == null ? EXCHANGE_NAME : exchangeName,
                    routingKeyPrefix + topic);
            }
        } catch (IOException e) {
            throw new DefinedRuntimeException(
                "unable create queue with routing key " + routingKeyPrefix + " and queue name " +
                    queueName, "0004",
                HttpResponseCode.NOT_HTTP,
                ExceptionCatalog.OPERATION_ERROR, e);
        }
        Channel finalChannel = channel;
        try {
            channel.basicConsume(queueName, autoAck, (consumerTag, delivery) -> {
                log.trace("mq message received");
                String s = new String(delivery.getBody(), StandardCharsets.UTF_8);
                StoredEvent storedEvent =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(s, StoredEvent.class);
                log.debug("handling {} with id {}",
                    ClassUtility.getShortName(storedEvent.getName()),
                    storedEvent.getId());
                long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                boolean consumeSuccess = true;
                try {
                    if (clazz == null) {
                        consumer1.accept(storedEvent);
                    } else {
                        T event = CommonDomainRegistry.getCustomObjectSerializer()
                            .deserialize(storedEvent.getEventBody(), clazz);
                        consumer2.accept(event);
                    }
                } catch (Exception ex) {
                    log.error(
                        "error during consume, catch error to maintain connection, reject message",
                        ex);
                    consumeSuccess = false;
                }
                if (consumeSuccess) {
                    finalChannel.basicAck(deliveryTag, false);
                } else {
                    finalChannel.basicNack(deliveryTag, false,
                        strategy.equals(ErrorHandleStrategy.REQUEUE));
                }
                log.trace("mq message consumed");
            }, consumerTag -> {
            });
        } catch (IOException e) {
            throw new DefinedRuntimeException(
                "unable consume message with routing key " + routingKeyPrefix + " and queue name " +
                    queueName, "0005",
                HttpResponseCode.NOT_HTTP,
                ExceptionCatalog.OPERATION_ERROR);
        }
    }

    @Override
    public <T extends DomainEvent> void replyOf(String subscribedApplicationName, boolean internal,
                                                String eventName, Class<T> clazz,
                                                Consumer<T> consumer) {
        listen(subscribedApplicationName, internal, MqHelper.handleReplyOf(appName, eventName),
            clazz, consumer, MqHelper.replyOf(eventName));
    }

    @Override
    public <T extends DomainEvent> void replyCancelOf(String subscribedApplicationName,
                                                      boolean internal, String eventName,
                                                      Class<T> clazz,
                                                      Consumer<T> consumer) {
        listen(subscribedApplicationName, internal,
            MqHelper.handleReplyCancelOf(appName, eventName), clazz, consumer,
            MqHelper.replyCancelOf(eventName));
    }

    @Override
    public <T extends DomainEvent> void cancelOf(String subscribedApplicationName, boolean internal,
                                                 String eventName, Class<T> clazz,
                                                 Consumer<T> consumer) {
        listen(subscribedApplicationName, internal, MqHelper.handleCancelOf(appName, eventName),
            clazz, consumer, MqHelper.cancelOf(eventName));
    }

    @Override
    public <T extends DomainEvent> void of(String subscribedApplicationName, boolean internal,
                                           String eventName, Class<T> clazz,
                                           Consumer<T> consumer) {
        listen(subscribedApplicationName, internal, MqHelper.handlerOf(appName, eventName),
            clazz, consumer, eventName);
    }

    @Override
    public void next(String appId, boolean internal, String topic, StoredEvent event) {
        String routingKey = appId + "." + (internal ? "internal" : "external") + "." + topic;
        log.debug("publish next event id {} with routing key {}", event.getId(), routingKey);
        Thread thread = Thread.currentThread();

        Channel channel = pubChannel.get(thread);
        if (channel == null) {
            try {
                channel = connectionPub.createChannel();
                //async publish confirm for best performance
                channel.confirmSelect();
                ConfirmCallback ackCallback = (sequenceNumber, multiple) -> {
                    Consumer<StoredEvent> markAsSent = (storedEvent) -> {
                        CommonApplicationServiceRegistry.getStoredEventApplicationService()
                            .markAsSent(storedEvent);
                    };
                    if (multiple) {
                        log.debug("ack callback with multiple confirm");
                        ConcurrentNavigableMap<Long, StoredEvent> confirmed =
                            outstandingConfirms.headMap(
                                sequenceNumber, true
                            );
                        confirmed.values().forEach(markAsSent);
                        confirmed.clear();
                    } else {
                        log.debug("ack callback with single confirm");
                        StoredEvent storedEvent = outstandingConfirms.get(sequenceNumber);
                        markAsSent.accept(storedEvent);
                        outstandingConfirms.remove(sequenceNumber);
                    }
                };
                //in case of failure, just clear outstandingConfirms and do nothing
                ConfirmCallback nAckCallback = (sequenceNumber, multiple) -> {
                    StoredEvent body = outstandingConfirms.get(sequenceNumber);
                    log.error(
                        "message with body {} has been nack-ed. sequence number: {}, multiple: {}",
                        body, sequenceNumber, multiple);
                    if (multiple) {
                        ConcurrentNavigableMap<Long, StoredEvent> confirmed =
                            outstandingConfirms.headMap(
                                sequenceNumber, true
                            );
                        confirmed.clear();
                    } else {
                        outstandingConfirms.remove(sequenceNumber);
                    }
                };
                channel.addConfirmListener(
                    ackCallback,
                    nAckCallback);
            } catch (IOException e) {
                throw new DefinedRuntimeException(
                    "unable create channel for " + appId + " with routing key " + routingKey,
                    "0006",
                    HttpResponseCode.NOT_HTTP,
                    ExceptionCatalog.OPERATION_ERROR, e);
            }
            pubChannel.put(thread, channel);
        }
        try {
            String body = CommonDomainRegistry.getCustomObjectSerializer().serialize(event);
            checkExchange(channel);
            outstandingConfirms.put(channel.getNextPublishSeqNo(), event);
            channel.basicPublish(EXCHANGE_NAME, routingKey, true,
                null, body.getBytes(StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            //when msg has no matching route and alternate exchange is also down
            throw new DefinedRuntimeException(
                "unable publish message for " + appId + " with routing key " + routingKey, "0007",
                HttpResponseCode.NOT_HTTP,
                ExceptionCatalog.OPERATION_ERROR, e);
        }
    }

    @Override
    public void next(StoredEvent event) {
        next(event.getApplicationId(), event.isInternal(), event.getTopic(), event);
    }

    private void checkExchange(Channel channel) throws IOException {
        Map<String, Object> args = new HashMap<>();
        args.put("alternate-exchange", EXCHANGE_NAME_ALT);

        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true, false, args);
        channel.exchangeDeclare(EXCHANGE_NAME_ALT, "fanout", true, false, null);
        channel.exchangeDeclare(EXCHANGE_NAME_REJECT, "fanout", true, false, null);
        channel.exchangeDeclare(EXCHANGE_NAME_DELAY, "fanout", true, false, null);

        channel.queueDeclare(QUEUE_NAME_ALT, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME_REJECT, true, false, false, null);

        Map<String, Object> args2 = new HashMap<>();
        args2.put("x-dead-letter-exchange", EXCHANGE_NAME);
        args2.put("x-message-ttl", 60 * 1000);//redeliver after 60s
        channel.queueDeclare(QUEUE_NAME_DELAY, true, false, false, args2);

        channel.queueBind(QUEUE_NAME_ALT, EXCHANGE_NAME_ALT, "");
        channel.queueBind(QUEUE_NAME_REJECT, EXCHANGE_NAME_REJECT, "");
        channel.queueBind(QUEUE_NAME_DELAY, EXCHANGE_NAME_DELAY, "");
    }

    @EventListener(ApplicationReadyEvent.class)
    private void unroutableMsgListener() {
        log.debug("subscribe for unroutable msg");
        this.subscribe(EXCHANGE_NAME_ALT, "", QUEUE_NAME_ALT, false, (event) -> {
            CommonApplicationServiceRegistry.getStoredEventApplicationService()
                .markAsUnroutable(event);
        }, ErrorHandleStrategy.MANUAL, "");
    }

    @EventListener(ApplicationReadyEvent.class)
    private void rejectedMsgListener() {
        log.debug("subscribe for rejected msg");
        this.subscribe(EXCHANGE_NAME_REJECT, "", QUEUE_NAME_REJECT, false, (event) -> {
            CommonApplicationServiceRegistry.getStoredEventApplicationService()
                .recordRejectedEvent(event);
        }, ErrorHandleStrategy.MANUAL, "");
    }
}
