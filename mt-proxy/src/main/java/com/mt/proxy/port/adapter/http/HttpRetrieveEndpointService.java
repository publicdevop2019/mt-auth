package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.Endpoint;
import com.mt.proxy.domain.RetrieveEndpointService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpRetrieveEndpointService implements RetrieveEndpointService {

    @Value("${manytree.url.endpoint}")
    private String endpointUrl;
    @Autowired
    private HttpHelper httpHelper;

    @Override
    public Set<Endpoint> loadAllEndpoints() {
        String base = httpHelper.resolveAccessPath();
        String url = base + endpointUrl;
        return  httpHelper.loadAllData(url, 40, false ,new ParameterizedTypeReference<>() {
        });
    }
}
