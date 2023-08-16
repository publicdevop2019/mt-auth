package com.mt.proxy.port.adapter.messaging;

import static com.mt.proxy.infrastructure.AppConstant.MT_ACCESS_ID;

import com.mt.proxy.domain.DomainRegistry;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EndpointMqListener {

    public static final String MT_GLOBAL_EXCHANGE = "mt_global_exchange";

    private EndpointMqListener(@Value("${mt.common.url.message-queue}") String url) {
        ConnectionFactory factory = new ConnectionFactory();
        String[] split = url.split(":");
        factory.setHost(split[0]);
        if (split.length > 1) {
            factory.setPort(Integer.parseInt(split[1]));
        }
        try {
            Connection connection = factory.newConnection("mt-proxy-sub");
            Channel channel = connection.createChannel();
            channel.basicQos(1);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, MT_GLOBAL_EXCHANGE,
                MT_ACCESS_ID + ".external.started_access");
            channel.queueBind(queueName, MT_GLOBAL_EXCHANGE,
                MT_ACCESS_ID + ".external.endpoint_reload_requested");
            channel.queueBind(queueName, MT_GLOBAL_EXCHANGE,
                MT_ACCESS_ID + ".external.endpoint_collection_modified");
            channel.queueBind(queueName, MT_GLOBAL_EXCHANGE,
                MT_ACCESS_ID + ".external.client_path_changed");
            channel.queueBind(queueName, MT_GLOBAL_EXCHANGE,
                MT_ACCESS_ID + ".external.sub_req_approved");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    //use auto ack, since admin will have to trigger sync job to match
                    DomainRegistry.getProxyCacheService().triggerReload();
                } catch (Exception ex) {
                    log.error("error in mq, error will not throw to keep mq connection", ex);
                }
                log.info("cache refresh requested");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            log.error("error in mq", e);
        }
    }
}
