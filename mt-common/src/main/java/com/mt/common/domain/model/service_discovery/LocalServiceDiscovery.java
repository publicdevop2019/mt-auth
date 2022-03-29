package com.mt.common.domain.model.service_discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
    value = "mt.service_discovery",
    havingValue = "false"
)
public class LocalServiceDiscovery implements ServiceDiscovery {
    @Autowired
    LocalServiceDiscoveryProperties localServiceDiscoveryProperties;

    @Override
    public String getApplicationUrl(String appName) {
        String s = localServiceDiscoveryProperties.getLocal().get(appName);
        log.debug("application {} URL resolved as {}", appName, s);
        return s;
    }
}
