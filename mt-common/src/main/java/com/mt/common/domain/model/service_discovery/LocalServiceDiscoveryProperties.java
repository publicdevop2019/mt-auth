package com.mt.common.domain.model.service_discovery;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "mt.discovery.config")
@ConditionalOnProperty(
    value = "mt.service_discovery",
    havingValue = "false"
)
public class LocalServiceDiscoveryProperties {
    @Getter
    @Setter
    private Map<String, String> local;
}
