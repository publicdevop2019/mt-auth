package com.mt.proxy.infrastructure.spring_cloud_gateway;

import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
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

    @Bean
    public IRule ribbonRule() {
        return new AvailabilityFilteringRule();
    }

}
