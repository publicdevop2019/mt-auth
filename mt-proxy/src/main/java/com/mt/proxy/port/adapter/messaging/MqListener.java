package com.mt.proxy.port.adapter.messaging;

import static com.mt.proxy.domain.ProxyCacheService.CACHE_LOG_PREFIX;

import com.mt.proxy.domain.DomainRegistry;
import com.mt.proxy.domain.InstanceInfo;
import com.mt.proxy.domain.UniqueIdGeneratorService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqListener {
    private static final String MT_GLOBAL_EXCHANGE = "mt_global_exchange";
    @Value("${mt.rabbitmq.url}")
    private String url;
    @Autowired
    private UniqueIdGeneratorService idGeneratorService;
    @Autowired
    private InstanceInfo instanceInfo;

    public void init() {
        if (Boolean.TRUE.equals(instanceInfo.getMqConnected())) {
            return;
        }
        synchronized (MqListener.class) {
            if (Boolean.TRUE.equals(instanceInfo.getMqConnected())) {
                return;
            }
            log.info("init mq");
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
                long id = idGeneratorService.id();
                String queueName = Long.toString(id, 36) + "_combined_events_proxy_handler";
                channel.queueDeclare(queueName, true, false, true, null);
                channel.queueBind(queueName, MT_GLOBAL_EXCHANGE, "endpoint_reload_requested");
                channel.queueBind(queueName, MT_GLOBAL_EXCHANGE, "endpoint_collection_modified");
                channel.queueBind(queueName, MT_GLOBAL_EXCHANGE, "client_path_changed");
                channel.queueBind(queueName, MT_GLOBAL_EXCHANGE, "sub_req_approved");
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    try {
                        //use auto ack, since admin will have to trigger sync job to match
                        DomainRegistry.getProxyCacheService().triggerReload();
                    } catch (Exception ex) {
                        log.error("error in mq, error will not throw to keep mq connection", ex);
                    }
                    log.info("{} refresh requested", CACHE_LOG_PREFIX);
                };
                channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
                });
            } catch (IOException | TimeoutException e) {
                log.warn("error during my init", e);
            }
            instanceInfo.setMqConnected(true);
        }
    }
}
