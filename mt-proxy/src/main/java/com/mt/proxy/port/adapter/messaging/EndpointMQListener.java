package com.mt.proxy.port.adapter.messaging;

import com.mt.proxy.domain.DomainRegistry;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class EndpointMQListener {
    private EndpointMQListener(@Value("${mt.url.support.mq}") String url) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(url);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("mt_global_exchange", "topic");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "mt_global_exchange", "access.external.started_access");
            channel.queueBind(queueName, "mt_global_exchange", "access.external.endpoint_reload_requested");
            channel.queueBind(queueName, "mt_global_exchange", "access.external.endpoint_collection_modified");
            channel.queueBind(queueName, "mt_global_exchange", "access.external.client_path_changed");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                log.debug("start refresh cached endpoints");
                DomainRegistry.getProxyCacheService().reloadProxyCache();
                log.debug("cached endpoints refreshed");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            log.error("error in mq", e);
        }
    }
}
