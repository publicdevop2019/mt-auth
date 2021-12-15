package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.Endpoint;

import java.util.Set;

public interface EndpointAdapter {
    Set<Endpoint> fetchAllEndpoints();
}
