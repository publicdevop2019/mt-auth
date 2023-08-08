package com.mt.common.infrastructure;

import static com.mt.common.CommonConstant.EXCHANGE_NAME;
import static com.mt.common.CommonConstant.EXCHANGE_NAME_ALT;
import static com.mt.common.CommonConstant.EXCHANGE_NAME_DELAY;
import static com.mt.common.CommonConstant.EXCHANGE_NAME_REJECT;
import static com.mt.common.CommonConstant.QUEUE_NAME_ALT;
import static com.mt.common.CommonConstant.QUEUE_NAME_DELAY;
import static com.mt.common.CommonConstant.QUEUE_NAME_REJECT;
import static com.mt.common.domain.model.constant.AppInfo.SPAN_ID_LOG;
import static com.mt.common.domain.model.constant.AppInfo.TRACE_ID_LOG;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.develop.Analytics;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.MqHelper;
import com.mt.common.domain.model.domain_event.SagaEventStreamService;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMqEventStreamService implements SagaEventStreamService {
    @Autowired
    @Qualifier("event-sub")
    private ThreadPoolExecutor eventSubExecutor;
    @Autowired
    @Qualifier("mark-event")
    private ThreadPoolExecutor markEventExecutor;
    @Autowired
    @Qualifier("event-pub")
    private ThreadPoolExecutor eventPubExecutor;
    public static final Map<Thread, Channel> pubChannel = new HashMap<>();
    public static final Map<Thread, Channel> subChannel = new HashMap<>();
    private final Connection connectionPub;
    private final Connection connectionSub;
    private Map<Thread, ConcurrentNavigableMap<Long, StoredEvent>> pendingConfirms =
        new HashMap<>();
    private Map<Thread, ConcurrentNavigableMap<Long, Long>> pendingConfirmsStartAt =
        new HashMap<>();
    private static final HashSet<String> reservedQueue = new HashSet<>();

    static {
        reservedQueue.add(QUEUE_NAME_ALT);
        reservedQueue.add(QUEUE_NAME_REJECT);
        reservedQueue.add(QUEUE_NAME_DELAY);
    }

    public RabbitMqEventStreamService(
        @Value("${mt.common.url.message-queue}") final String url,
        @Autowired @Qualifier("event-exe") ThreadPoolExecutor eventExePoolExecutor
    ) {
        log.debug("initializing event stream service with url {}", url);
        ConnectionFactory factory = new ConnectionFactory();
        String[] split = url.split(":");
        factory.setHost(split[0]);
        if (split.length > 1) {
            factory.setPort(Integer.parseInt(split[1]));
        }
        try {
            connectionSub = factory.newConnection(eventExePoolExecutor, "mt-access-sub");
        } catch (IOException | TimeoutException ex) {
            throw new DefinedRuntimeException("unable to create subscribe connection", "0000",
                HttpResponseCode.NOT_HTTP, ex);
        }
        try {
            connectionPub = factory.newConnection("mt-access-pub");
        } catch (IOException | TimeoutException e) {
            log.error("unable to create publish connection", e);
            try {
                connectionSub.close();
            } catch (IOException ex) {
                throw new DefinedRuntimeException("error during close subscribe connection", "0001",
                    HttpResponseCode.NOT_HTTP, ex);
            }
            throw new DefinedRuntimeException("unable to create publish connection", "0002",
                HttpResponseCode.NOT_HTTP, e);
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
        //using pool to avoid single thread get all queue bindings
        eventSubExecutor.execute(() -> {
            Thread thread = Thread.currentThread();
            log.debug("{} subscribing to queue {}", thread.getName(), queueName);
            Channel channel = subChannel.get(thread);
            if (channel == null) {
                log.debug("initiating consumer channel");
                try {
                    channel = connectionSub.createChannel();
                    checkExchange(channel);
                } catch (IOException e) {
                    throw new DefinedRuntimeException(
                        "unable create subscribe channel with routing key " + routingKeyPrefix +
                            " and queue name " + queueName, "0003",
                        HttpResponseCode.NOT_HTTP, e);
                }
                subChannel.put(thread, channel);
                try {
                    if (reservedQueue.contains(queueName)) {
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
                    channel.basicQos(250);
                    for (String topic : topics) {
                        channel.queueBind(queueName,
                            exchangeName == null ? EXCHANGE_NAME : exchangeName,
                            routingKeyPrefix + topic);
                    }
                } catch (IOException e) {
                    throw new DefinedRuntimeException(
                        "unable create queue with routing key " + routingKeyPrefix +
                            " and queue name " +
                            queueName, "0004",
                        HttpResponseCode.NOT_HTTP, e);
                }
            }
            try {
                Channel finalChannel = channel;
                channel.basicConsume(queueName, false, (consumerTag, delivery) -> {
                    MDC.put(SPAN_ID_LOG,
                        CommonDomainRegistry.getUniqueIdGeneratorService().idString());
                    StoredEvent storedEvent =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .deserialize(new String(delivery.getBody(), StandardCharsets.UTF_8),
                                StoredEvent.class);
                    MDC.put(TRACE_ID_LOG, storedEvent.getTraceId());
                    long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                    if (log.isDebugEnabled()) {
                        log.debug(
                            "tag {} handling {} with delivery id {} event id {} ",
                            consumerTag,
                            storedEvent.getName(),
                            deliveryTag,
                            storedEvent.getId());
                    }
                    Analytics.stopEvent(consumerTag, storedEvent);
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
                    log.debug("replying delivery tag {}, result {}, channel number {}", deliveryTag,
                        consumeSuccess, finalChannel.getChannelNumber());
                    if (consumeSuccess) {
                        finalChannel.basicAck(deliveryTag, false);
                    } else {
                        finalChannel.basicNack(deliveryTag, false,
                            strategy.equals(ErrorHandleStrategy.REQUEUE));
                    }
                }, consumerTag -> {
                });
            } catch (IOException e) {
                throw new DefinedRuntimeException(
                    "unable consume message with routing key " + routingKeyPrefix +
                        " and queue name " +
                        queueName, "0005",
                    HttpResponseCode.NOT_HTTP);
            }
        });

    }

    @Override
    public <T extends DomainEvent> void replyOf(String subAppName, boolean internal,
                                                String eventName, Class<T> clazz,
                                                Consumer<T> consumer) {
        listen(subAppName, internal, MqHelper.handleReplyOf(subAppName, eventName),
            clazz, consumer, MqHelper.replyOf(eventName));
    }

    @Override
    public <T extends DomainEvent> void replyCancelOf(String subAppName,
                                                      boolean internal, String eventName,
                                                      Class<T> clazz,
                                                      Consumer<T> consumer) {
        listen(subAppName, internal,
            MqHelper.handleReplyCancelOf(subAppName, eventName), clazz, consumer,
            MqHelper.replyCancelOf(eventName));
    }

    @Override
    public <T extends DomainEvent> void cancelOf(String subAppName, boolean internal,
                                                 String eventName, Class<T> clazz,
                                                 Consumer<T> consumer) {
        listen(subAppName, internal, MqHelper.handleCancelOf(subAppName, eventName),
            clazz, consumer, MqHelper.cancelOf(eventName));
    }

    @Override
    public <T extends DomainEvent> void of(String subAppName, boolean internal,
                                           String eventName, Class<T> clazz,
                                           Consumer<T> consumer) {
        listen(subAppName, internal, MqHelper.handlerOf(subAppName, eventName),
            clazz, consumer, eventName);
    }

    @Override
    public void next(StoredEvent event) {
        next(event.getApplicationId(), event.getInternal(), event.getTopic(), event);
    }

    @Override
    public void next(String appId, boolean internal, String topic, StoredEvent event) {
        log.debug("before submit event {} with id {} to pub pool", event.getName(),
            event.getId());
        Analytics start = Analytics.start(Analytics.Type.EVENT_START_PUBLISH);
        if (log.isTraceEnabled()) {
            int size = eventPubExecutor.getQueue().size();
            int activeCount = eventPubExecutor.getActiveCount();
            long completedTaskCount = eventPubExecutor.getCompletedTaskCount();
            int poolSize = eventPubExecutor.getPoolSize();
            log.debug(
                "event submit pool status: queue size {} active count {} completed task count {} pool size {}",
                size, activeCount, completedTaskCount, poolSize);
        }
        eventPubExecutor.execute(() -> {
            MDC.put(TRACE_ID_LOG, event.getTraceId());
            log.debug("publishing event");
            start.stop();
            Analytics pubAnalytics = Analytics.start(Analytics.Type.EVENTS_PUBLISH);
            Thread currentThread = Thread.currentThread();
            String routingKey = appId + "." + (internal ? "internal" : "external") + "." + topic;
            Channel channel = pubChannel.get(currentThread);
            ConcurrentNavigableMap<Long, StoredEvent> outstandingConfirms =
                pendingConfirms.get(currentThread);
            ConcurrentNavigableMap<Long, Long> outstandingConfirmsStartAt =
                pendingConfirmsStartAt.get(currentThread);
            if (channel == null) {
                log.debug("initiating publisher related resources");
                try {
                    channel = connectionPub.createChannel();
                    channel.confirmSelect();
                    checkExchange(channel);
                    log.debug("complete exchange check");
                } catch (IOException e) {
                    throw new DefinedRuntimeException(
                        "unable create channel for " + appId + " with routing key " + routingKey,
                        "0006",
                        HttpResponseCode.NOT_HTTP, e);
                }
                pubChannel.put(currentThread, channel);
                outstandingConfirms = new ConcurrentSkipListMap<>();
                pendingConfirms.put(currentThread, outstandingConfirms);
                outstandingConfirmsStartAt = new ConcurrentSkipListMap<>();
                pendingConfirmsStartAt.put(currentThread, outstandingConfirmsStartAt);
                String name = currentThread.getName();
                //async publish confirm for best performance
                //NOTE do not move below two lambda as class variable
                ConcurrentNavigableMap<Long, StoredEvent> finalOutstandingConfirms =
                    outstandingConfirms;
                ConcurrentNavigableMap<Long, Long> finalOutstandingConfirmsStartAt =
                    outstandingConfirmsStartAt;
                ConfirmCallback confirmCallback = (sequenceNumber, multiple) -> {
                    log.debug("confirm callback, published by {} sequence number {} multiple {}",
                        name,
                        sequenceNumber, multiple);
                    Consumer<StoredEvent> markAsSent = (storedEvent) -> {
                        markEventExecutor.submit(() -> {
                            MDC.put(TRACE_ID_LOG, event.getTraceId());
                            log.debug("marking stored event id {} as sent", storedEvent.getId());
                            Analytics markEvent = Analytics.start(Analytics.Type.MARK_EVENT);
                            CommonApplicationServiceRegistry.getStoredEventApplicationService()
                                .markAsSent(storedEvent);
                            markEvent.stop();
                            MDC.clear();
                        });
                    };
                    if (multiple) {
                        ConcurrentNavigableMap<Long, StoredEvent> confirmed =
                            finalOutstandingConfirms.headMap(
                                sequenceNumber, true
                            );
                        ConcurrentNavigableMap<Long, Long> confirmedStartAt =
                            finalOutstandingConfirmsStartAt.headMap(
                                sequenceNumber, true
                            );
                        log.debug("batch confirming, sequence number {}", sequenceNumber);
                        log.debug("confirm event count {}", confirmed.values().size());
                        log.debug("confirm start at count {}", confirmedStartAt.values().size());
                        confirmed.values().forEach(markAsSent);
                        confirmedStartAt.forEach((sequenceId, startAt) -> {
                            StoredEvent storedEvent = confirmed.get(sequenceId);
                            if (startAt == null) {
                                log.error("unable to find startAt time");
                            } else {
                                Analytics.stopPublish(startAt, storedEvent, sequenceId);
                            }
                        });
                        confirmed.clear();
                        confirmedStartAt.clear();
                    } else {
                        StoredEvent storedEvent = finalOutstandingConfirms.get(sequenceNumber);
                        Long startAt = finalOutstandingConfirmsStartAt.get(sequenceNumber);
                        if (startAt == null) {
                            log.warn("unable to find startAt time");
                        } else {
                            Analytics.stopPublish(startAt, storedEvent, sequenceNumber);
                        }
                        if (storedEvent != null) {
                            markAsSent.accept(storedEvent);
                        } else {
                            log.error(
                                "unable to find stored event, sequence number {}",
                                sequenceNumber);
                        }
                        finalOutstandingConfirms.remove(sequenceNumber);
                    }
                };

                //in case of failure, just clear outstandingConfirms and do nothing
                ConfirmCallback rejectCallback = (sequenceNumber, multiple) -> {
                    log.debug("reject callback, published by {}", name);
                    StoredEvent body = finalOutstandingConfirms.get(sequenceNumber);
                    log.error(
                        "message with body {} has been nack-ed. sequence number: {}, multiple: {}",
                        body, sequenceNumber, multiple);
                    if (multiple) {
                        ConcurrentNavigableMap<Long, StoredEvent> confirmed =
                            finalOutstandingConfirms.headMap(
                                sequenceNumber, true
                            );
                        ConcurrentNavigableMap<Long, Long> confirmedStartAt =
                            finalOutstandingConfirmsStartAt.headMap(
                                sequenceNumber, true
                            );
                        confirmed.clear();
                        confirmedStartAt.clear();
                    } else {
                        finalOutstandingConfirms.remove(sequenceNumber);
                        finalOutstandingConfirmsStartAt.remove(sequenceNumber);
                    }
                };
                channel.addConfirmListener(
                    confirmCallback,
                    rejectCallback);
            }
            //publish msg
            try {
                log.trace("before publish message");
                String body = CommonDomainRegistry.getCustomObjectSerializer().serialize(event);
                log.trace("complete serialization");
                long nextPublishSeqNo = channel.getNextPublishSeqNo();
                log.debug("put outstanding confirms {} and {}", nextPublishSeqNo, event.getId());
                outstandingConfirms.put(nextPublishSeqNo, event);
                outstandingConfirmsStartAt.put(nextPublishSeqNo,
                    Instant.now().toEpochMilli());
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                channel.basicPublish(EXCHANGE_NAME, routingKey, true,
                    null,bytes
                );
                log.debug("channel num {} published next event id {} with routing key {} size {}(bytes)",
                    channel.getChannelNumber(), event.getId(), routingKey, bytes.length);
                pubAnalytics.stop();
            } catch (IOException e) {
                //when msg has no matching route and alternate exchange is also down
                throw new DefinedRuntimeException(
                    "unable publish message for " + appId + " with routing key " + routingKey,
                    "0007",
                    HttpResponseCode.NOT_HTTP, e);
            }
        });
    }


    private void checkExchange(Channel channel) throws IOException {
        Map<String, Object> args = new HashMap<>();
        args.put("alternate-exchange", EXCHANGE_NAME_ALT);
        log.trace("exchange declare");
        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true, false, args);
        channel.exchangeDeclare(EXCHANGE_NAME_ALT, "fanout", true, false, null);
        channel.exchangeDeclare(EXCHANGE_NAME_REJECT, "fanout", true, false, null);
        channel.exchangeDeclare(EXCHANGE_NAME_DELAY, "fanout", true, false, null);
        log.trace("queue declare");
        channel.queueDeclare(QUEUE_NAME_ALT, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME_REJECT, true, false, false, null);

        Map<String, Object> args2 = new HashMap<>();
        args2.put("x-dead-letter-exchange", EXCHANGE_NAME);
        args2.put("x-message-ttl", 60 * 1000);//redeliver after 60s
        channel.queueDeclare(QUEUE_NAME_DELAY, true, false, false, args2);
        log.trace("queue bind");
        channel.queueBind(QUEUE_NAME_ALT, EXCHANGE_NAME_ALT, "");
        channel.queueBind(QUEUE_NAME_REJECT, EXCHANGE_NAME_REJECT, "");
        channel.queueBind(QUEUE_NAME_DELAY, EXCHANGE_NAME_DELAY, "");
    }

    @EventListener(ApplicationReadyEvent.class)
    private void unroutableMsgListener() {
        this.subscribe(EXCHANGE_NAME_ALT, "", QUEUE_NAME_ALT, false, (event) -> {
            CommonApplicationServiceRegistry.getStoredEventApplicationService()
                .markAsUnroutable(event);
        }, ErrorHandleStrategy.MANUAL, "");
    }

    @EventListener(ApplicationReadyEvent.class)
    private void rejectedMsgListener() {
        this.subscribe(EXCHANGE_NAME_REJECT, "", QUEUE_NAME_REJECT, false, (event) -> {
            CommonApplicationServiceRegistry.getStoredEventApplicationService()
                .recordRejectedEvent(event);
        }, ErrorHandleStrategy.MANUAL, "");
    }


}
