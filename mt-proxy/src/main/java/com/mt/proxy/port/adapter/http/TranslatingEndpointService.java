package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.Endpoint;
import com.mt.proxy.domain.RetrieveEndpointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class TranslatingEndpointService implements RetrieveEndpointService {
    @Autowired
    private EndpointAdapter endpointAdapter;

    @Override
    public Set<Endpoint> loadAllEndpoints() {
        Set<Endpoint> endpoints = endpointAdapter.fetchAllEndpoints();
        log.debug("load all endpoints started");
        log.debug("load all endpoints complete, total {} loaded", endpoints.size());
        return endpoints;
    }
}
