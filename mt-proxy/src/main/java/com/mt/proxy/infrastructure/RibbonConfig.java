package com.mt.proxy.infrastructure;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.PingUrl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * enable ribbon health check.
 */
@Configuration
public class RibbonConfig {

    @Bean
    public IPing ribbonPing() {
        return new PingUrl(false, "/health");
    }


}
