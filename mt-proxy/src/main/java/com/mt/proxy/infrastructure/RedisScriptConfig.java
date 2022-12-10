package com.mt.proxy.infrastructure;

import com.mt.proxy.domain.rate_limit.RateLimitResult;
import java.io.IOException;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisScriptConfig {
    @Bean
    public RedisScript<?> script() throws IOException {
        ResourceScriptSource resourceScriptSource =
            new ResourceScriptSource(new ClassPathResource("redis_rate_limiter.lua"));
        return RedisScript.of(resourceScriptSource.getScriptAsString(), List.class);
    }
}
