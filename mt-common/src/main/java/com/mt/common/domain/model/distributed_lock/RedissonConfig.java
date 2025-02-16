package com.mt.common.domain.model.distributed_lock;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.connection.ConnectionEventsHub;
import org.redisson.connection.ConnectionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RedissonConfig {
    @Value("${mt.redis.url}")
    private String url;
    @Value("${mt.redis.password:#{null}}")
    private String password;
    @Value("${mt.redis.client-name:#{null}}")
    private String name;

    @Bean
    public RedissonClient configRedisson(MeterRegistry meterRegistry) {
        log.debug("start of configure redisson");
        Config config = new Config();
        config
            .useSingleServer()
            .setAddress(url)
            .setPassword(password)
            .setClientName(name)
            .setTimeout(1000)
        ;
        RedissonClient redissonClient = Redisson.create(config);
        monitorRedissonMetrics(redissonClient, meterRegistry);
        return redissonClient;
    }

    private void monitorRedissonMetrics(RedissonClient redisson,
                                        MeterRegistry registry) {
        ConnectionEventsHub connectionEventsHub =
            ((Redisson) redisson).getConnectionManager().getConnectionEventsHub();
        Counter connectCounter = Counter.builder("redisson-connect").register(registry);
        Counter disconnectCounter = Counter.builder("redisson-dis-connect").register(registry);

        ConnectionListener connectionListener = new ConnectionListener() {
            @Override
            public void onConnect(InetSocketAddress addr) {
                log.debug("on connect");
                connectCounter.increment();
            }

            @Override
            public void onDisconnect(InetSocketAddress addr) {
                log.debug("on dis-connect");
                disconnectCounter.increment();
            }
        };
        connectionEventsHub.addListener(connectionListener);
    }
}
