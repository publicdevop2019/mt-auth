package com.mt.common.domain.model.distributed_lock;

import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    public RedissonClient configRedisson() {
        log.debug("start of configure redisson");
        Config config = new Config();
        config
            .useSingleServer()
            .setAddress(url)
            .setPassword(password)
            .setClientName(name)
            .setTimeout(1000)
        ;
        return Redisson.create(config);
    }
}
