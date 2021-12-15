package com.mt.common.domain.model.service_discovery;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EurekaHelper {
    @Autowired
    private EurekaClient discoveryClient;

    public String getApplicationUrl(String appName) {
        Application application1 = discoveryClient.getApplication(appName.toUpperCase());
        String homePageUrl = application1.getInstances().get(0).getHomePageUrl();
        log.debug("application {} URL resolved as {}", appName, homePageUrl);
        return homePageUrl;
    }
}
