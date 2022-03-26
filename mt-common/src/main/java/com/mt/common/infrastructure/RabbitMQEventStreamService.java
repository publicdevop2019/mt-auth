package com.mt.common.infrastructure;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.clazz.ClassUtility;
import com.mt.common.domain.model.domain_event.MQHelper;
import com.mt.common.domain.model.domain_event.SagaEventStreamService;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static com.mt.common.CommonConstant.EXCHANGE_NAME;

@Slf4j
@Component
public class RabbitMQEventStreamService implements SagaEventStreamService {

    @Value("${spring.application.name}")
    private String appName;
    @Resource
    private Environment env;
    private Connection connectionPub;
    private Connection connectionSub;

    public RabbitMQEventStreamService(@Value("${mt.url.support.mq}") final String url) {
        log.debug("start of configure rabbitmq with url {}", url);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(url);
        try {
            connectionPub = factory.newConnection();
            connectionSub = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            log.error("unable to publish message to rabbitmq", e);
            try {
                connectionPub.close();
            } catch (IOException ex) {
                log.error("error during close pub connection", ex);
            }
            try {
                connectionSub.close();
            } catch (IOException ex) {
                log.error("error during close sub connection", ex);
            }
        }
        log.debug("end of configure rabbitmq");
    }

    @Override
    public void subscribe(String subscribedApplicationName, boolean internal, @Nullable String fixedQueueName, Consumer<StoredEvent> consumer, String... topics) {
        String routingKeyWithoutTopic = subscribedApplicationName + "." + (internal ? "internal" : "external") + ".";
        String queueName;
        if (fixedQueueName != null) {
            queueName = fixedQueueName;
        } else {
            long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
            queueName = Long.toString(id, 36);
        }
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            log.trace("mq message received");
            String s = new String(delivery.getBody(), StandardCharsets.UTF_8);
            StoredEvent event = CommonDomainRegistry.getCustomObjectSerializer().deserialize(s, StoredEvent.class);
            log.debug("handling {} with id {}", ClassUtility.getShortName(event.getName()), event.getId());
            try {
                consumer.accept(event);
            } catch (Exception ex) {
                log.error("error during consume, catch error to maintain connection", ex);
            }
            log.trace("mq message consumed");
        };
        try {
            Channel channel = connectionSub.createChannel();
            channel.queueDeclare(queueName, true, false, false, null);
            checkExchange(channel);
            for (String topic : topics) {
                channel.queueBind(queueName, EXCHANGE_NAME, routingKeyWithoutTopic + topic);
            }
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            log.error("unable create queue for {} with routing key {} and queue name {}", subscribedApplicationName, routingKeyWithoutTopic, queueName, e);
        }
    }

    @Override
    public void replyOf(String subscribedApplicationName, boolean internal, String eventName, Consumer<StoredEvent> consumer) {
        subscribe(subscribedApplicationName, internal, MQHelper.handleReplyOf(appName, eventName), consumer, MQHelper.replyOf(eventName));
    }

    @Override
    public void replyCancelOf(String subscribedApplicationName, boolean internal, String eventName, Consumer<StoredEvent> consumer) {
        subscribe(subscribedApplicationName, internal, MQHelper.handleReplyCancelOf(appName, eventName), consumer, MQHelper.replyCancelOf(eventName));
    }

    @Override
    public void cancelOf(String subscribedApplicationName, boolean internal, String eventName, Consumer<StoredEvent> consumer) {
        subscribe(subscribedApplicationName, internal, MQHelper.handleCancelOf(appName, eventName), consumer, MQHelper.cancelOf(eventName));
    }

    @Override
    public void of(String subscribedApplicationName, boolean internal, String eventName, Consumer<StoredEvent> consumer) {
        subscribe(subscribedApplicationName, internal, MQHelper.handlerOf(appName, eventName), consumer, eventName);
    }

    @Override
    public void next(String appName, boolean internal, String topic, StoredEvent event) {
        String routingKey = appName + "." + (internal ? "internal" : "external") + "." + topic;
        log.debug("publish next event id {}with routing key {}", event.getId(), routingKey);
        try (Channel channel = connectionPub.createChannel()) {
            checkExchange(channel);
            channel.basicPublish(EXCHANGE_NAME, routingKey,
                    null,
                    CommonDomainRegistry.getCustomObjectSerializer().serialize(event).getBytes(StandardCharsets.UTF_8));
        } catch (IOException | TimeoutException e) {
            log.error("unable to publish message to rabbitmq", e);
        }
    }

    @Override
    public void next(StoredEvent event) {
        next(appName, event.isInternal(), event.getTopic(), event);
    }

    private void checkExchange(Channel channel) throws IOException {
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
    }
}
