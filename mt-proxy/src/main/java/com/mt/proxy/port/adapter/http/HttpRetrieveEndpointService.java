package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.Endpoint;
import com.mt.proxy.domain.RetrieveEndpointService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpRetrieveEndpointService implements RetrieveEndpointService {
    private static final String ENDPOINT_URL = "endpoints/proxy";

    @Autowired
    private HttpUtility httpHelper;

    @Override
    public Set<Endpoint> loadAllEndpoints() {
        return httpHelper.loadAllData(httpHelper.resolveAccessPath() + ENDPOINT_URL,
            40, false, new ParameterizedTypeReference<>() {
            });
    }
}
