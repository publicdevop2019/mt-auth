package com.hw.helper;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestHelper {
    @Autowired
    private EurekaClient eurekaClient;

    public String getAccessUrl(String path) {
        String normalized = removeLeadingSlash(path);
        if(eurekaClient.getApplication("ACCESS")==null){
            return UserAction.proxyUrl+"/auth-svc/"+normalized;
        }
        return eurekaClient.getApplication("ACCESS").getInstances().get(0).getHomePageUrl() + normalized;
    }

    public String getMallUrl(String path) {
        String normalized = removeLeadingSlash(path);
        return eurekaClient.getApplication("MALL").getInstances().get(0).getHomePageUrl() + normalized;
    }

    public String getUserProfileUrl(String path) {
        String normalized = removeLeadingSlash(path);
        return eurekaClient.getApplication("PROFILE").getInstances().get(0).getHomePageUrl() + normalized;
    }

    private String removeLeadingSlash(String path) {
        return path.replaceAll("^/+", "");
    }
}
