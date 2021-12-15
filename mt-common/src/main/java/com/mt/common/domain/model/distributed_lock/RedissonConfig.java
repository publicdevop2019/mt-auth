package com.mt.common.domain.model.distributed_lock;

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
@ConditionalOnProperty(
        value="mt.distributed_lock",
        havingValue = "true",
        matchIfMissing = true)
public class RedissonConfig {
    @Value("${mt.url.support.dis_lock}")
    private String url;

    @Bean
    public RedissonClient configRedisson() {
        log.debug("start of configure redisson");
        Config config = new Config();
        config.useSingleServer().setAddress(url);
        RedissonClient redisson = Redisson.create(config);
        return redisson;

    }
}
