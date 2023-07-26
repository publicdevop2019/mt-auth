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
import com.mt.common.domain.model.clazz.ClassUtility;
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
import java.util.concurrent.atomic.AtomicReference;
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
    @Qualifier("msg")
    private ThreadPoolExecutor subExecutor;
    public static final Map<Thread, Channel> pubChannel = new HashMap<>();
    public static final Map<Thread, Channel> subChannel = new HashMap<>();
    private final Boolean autoAck = false;
    private final Connection connectionPub;
    private final Connection connectionSub;
    ConcurrentNavigableMap<Long, StoredEvent> outstandingConfirms = new ConcurrentSkipListMap<>();
    private static final HashSet<String> reservedQueue = new HashSet<>();

    static {
        reservedQueue.add(QUEUE_NAME_ALT);
        reservedQueue.add(QUEUE_NAME_REJECT);
        reservedQueue.add(QUEUE_NAME_DELAY);
    }

    public RabbitMqEventStreamService(@Value("${mt.common.url.message-queue}") final String url) {
        log.debug("initializing event stream service with url {}", url);
        ConnectionFactory factory = new ConnectionFactory();
        String[] split = url.split(":");
        factory.setHost(split[0]);
        if (split.length > 1) {
            factory.setPort(Integer.parseInt(split[1]));
        }
        try {
            connectionSub = factory.newConnection("mt-access-sub");
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
        subExecutor.execute(() -> {
            Thread thread = Thread.currentThread();
            log.debug("{} subscribing to queue {}", thread.getName(), queueName);
            Channel channel = subChannel.get(thread);
            if (channel == null) {
                try {
                    channel = connectionSub.createChannel();
                } catch (IOException e) {
                    throw new DefinedRuntimeException(
                        "unable create subscribe channel with routing key " + routingKeyPrefix +
                            " and queue name " + queueName, "0003",
                        HttpResponseCode.NOT_HTTP, e);
                }
                subChannel.put(thread, channel);
            }
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
                checkExchange(channel);
                //TODO find out proper prefetch value, this requires test in prod env
                channel.basicQos(30);
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
            Channel finalChannel = channel;
            try {
                int channelNumber;
                if (log.isDebugEnabled()) {
                    channelNumber = channel.getChannelNumber();
                } else {
                    channelNumber = -1;
                }
                channel.basicConsume(queueName, autoAck, (consumerTag, delivery) -> {
                    String s = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    StoredEvent storedEvent =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .deserialize(s, StoredEvent.class);
                    MDC.put(TRACE_ID_LOG, storedEvent.getTraceId());
                    MDC.put(SPAN_ID_LOG,
                        CommonDomainRegistry.getUniqueIdGeneratorService().idString());
                    Analytics deliverAnalytic = Analytics.start(storedEvent);
                    log.trace("mq message received");
                    deliverAnalytic.stopEvent(channelNumber, consumerTag, storedEvent);
                    if (log.isDebugEnabled()) {
                        long l = Instant.now().toEpochMilli();
                        Long timestamp = storedEvent.getTimestamp();
                        log.debug(
                            "channel num {} tag {} handling {} with id {}, total time taken before consume is {} milli",
                            channelNumber,
                            consumerTag,
                            ClassUtility.getShortName(storedEvent.getName()),
                            storedEvent.getId(), l - timestamp);
                    }
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
    public void next(String appId, boolean internal, String topic, StoredEvent event) {
        AtomicReference<Analytics> publishAnalytic = new AtomicReference<>();
        MDC.put(TRACE_ID_LOG, event.getTraceId());
        String routingKey = appId + "." + (internal ? "internal" : "external") + "." + topic;
        //async publish confirm for best performance
        ConfirmCallback confirmCallback = (sequenceNumber, multiple) -> {
            publishAnalytic.get().stop();
            MDC.put(TRACE_ID_LOG, event.getTraceId());
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
                if (storedEvent != null) {
                    markAsSent.accept(storedEvent);
                } else {
                    log.warn(
                        "unable to find stored event, this may indicate some issue, sequenceNum {}",
                        sequenceNumber);
                }
                outstandingConfirms.remove(sequenceNumber);
            }
        };
        //in case of failure, just clear outstandingConfirms and do nothing
        ConfirmCallback rejectCallback = (sequenceNumber, multiple) -> {
            MDC.put(TRACE_ID_LOG, event.getTraceId());
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
        Thread thread = Thread.currentThread();
        Channel channel = pubChannel.get(thread);
        if (channel == null) {
            try {
                channel = connectionPub.createChannel();
                channel.confirmSelect();
            } catch (IOException e) {
                throw new DefinedRuntimeException(
                    "unable create channel for " + appId + " with routing key " + routingKey,
                    "0006",
                    HttpResponseCode.NOT_HTTP, e);
            }
            channel.addConfirmListener(
                confirmCallback,
                rejectCallback);
            pubChannel.put(thread, channel);
        }
        //publish msg
        try {
            String body = CommonDomainRegistry.getCustomObjectSerializer().serialize(event);
            checkExchange(channel);
            publishAnalytic.set(Analytics.start(Analytics.Type.EVENT_PUBLISH_CONFIRM));
            outstandingConfirms.put(channel.getNextPublishSeqNo(), event);
            log.debug("channel num {} publish next event id {} with routing key {}",
                channel.getChannelNumber(), event.getId(), routingKey);
            channel.basicPublish(EXCHANGE_NAME, routingKey, true,
                null, body.getBytes(StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            //when msg has no matching route and alternate exchange is also down
            throw new DefinedRuntimeException(
                "unable publish message for " + appId + " with routing key " + routingKey, "0007",
                HttpResponseCode.NOT_HTTP, e);
        }
    }

    @Override
    public void next(StoredEvent event) {
        next(event.getApplicationId(), event.getInternal(), event.getTopic(), event);
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
