package com.mt.proxy.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class InstanceInfo {
    private volatile Boolean running = false;
    private volatile Boolean jwtPublicCertLoaded = false;
    private volatile Boolean endpointsLoaded = false;
    private volatile Boolean routesLoaded = false;
    private volatile Boolean mqConnected = false;
    private volatile Integer id = null;
    @Value("${mt.misc.service-in:#{null}}")
    private volatile Boolean autoServiceIn = true;

    public boolean ready() {
        return id != null && endpointsLoaded && routesLoaded && jwtPublicCertLoaded && mqConnected && autoServiceIn;
    }
}
