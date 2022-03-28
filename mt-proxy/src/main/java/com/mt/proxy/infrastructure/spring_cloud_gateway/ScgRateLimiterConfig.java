package com.mt.proxy.infrastructure.spring_cloud_gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class ScgRateLimiterConfig {
    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> {
            if (exchange.getRequest().getRemoteAddress() != null) {
                return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().toString());
            }
            return Mono.just("unknown request");
        };
    }
}
