package com.mt.proxy.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
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
    @Value("${mt.redis.timeout}")
    private Integer timeout;

    @Bean
    public RedissonClient configRedisson() {
        log.debug("start of configure redisson");
        Config config = new Config();
        config
            .useSingleServer()
            .setAddress(url)
            .setPassword(password)
            .setClientName(name)
            .setTimeout(timeout)
        ;
        return Redisson.create(config);
    }
}
