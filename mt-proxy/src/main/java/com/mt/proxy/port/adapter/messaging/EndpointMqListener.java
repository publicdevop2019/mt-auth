package com.mt.proxy.port.adapter.messaging;

import static com.mt.proxy.infrastructure.AppConstant.MT_ACCESS_ID;

import com.mt.proxy.domain.DomainRegistry;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EndpointMqListener {
    private EndpointMqListener(@Value("${mt.url.support.mq}") String url,
                               @Value("${spring.application.name}") String name) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(url);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("mt_global_exchange", "topic");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "mt_global_exchange",
                MT_ACCESS_ID + ".external.started_access");
            channel.queueBind(queueName, "mt_global_exchange",
                MT_ACCESS_ID + ".external.endpoint_reload_requested");
            channel.queueBind(queueName, "mt_global_exchange",
                MT_ACCESS_ID + ".external.endpoint_collection_modified");
            channel.queueBind(queueName, "mt_global_exchange",
                MT_ACCESS_ID + ".external.client_path_changed");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                log.debug("start refresh cached endpoints");
                try {
                    DomainRegistry.getProxyCacheService().reloadProxyCache();
                } catch (Exception ex) {
                    log.error("error in mq, error will not throw to keep mq connection", ex);
                }
                log.debug("cached endpoints refreshed");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            log.error("error in mq", e);
        }
    }
}
