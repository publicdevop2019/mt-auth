package com.mt.proxy.domain;

import static com.mt.proxy.domain.Utility.isWebSocket;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Service
public class CacheService {
    private final Map<Endpoint, CacheConfiguration> configurationMap = new HashMap<>();

    public void refresh(Set<Endpoint> cached) {
        log.debug("refresh cache config");
        configurationMap.clear();
        cached.forEach(endpoint -> {
            if (endpoint.hasCacheInfo()) {
                CacheConfiguration configuration = new CacheConfiguration(endpoint);
                configurationMap
                    .put(endpoint, configuration);
            } else {
                configurationMap
                    .put(endpoint, null);
            }

        });
        log.debug("refresh cache config completed, cache configuration count is {}",
            configurationMap.size());
    }

    public CacheConfiguration getCacheConfiguration(ServerWebExchange exchange, boolean isEtag) {
        String method = exchange.getRequest().getMethodValue();
        String path;
        if (isEtag) {
            path = exchange.getRequest().getPath().value();
        } else {
            LinkedHashSet<URI> attribute = exchange.getAttribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
            Optional<URI> lb1 =
                attribute.stream().filter(e -> !e.getScheme().equals("lb")).findFirst();
            if (lb1.isPresent()) {
                path = lb1.get().getPath();
            } else {
                return null;
            }
        }
        Optional<Endpoint> endpoint = DomainRegistry.getEndpointService()
            .findEndpoint(path, method, isWebSocket(exchange.getRequest().getHeaders()));
        if (endpoint.isEmpty()) {
            log.debug("unable to find cors config due to missing endpoint");
            return null;
        }
        CacheConfiguration cacheConfiguration = this.configurationMap.get(endpoint.get());
        log.trace("found config {} for path {} with method {}", cacheConfiguration, path,
            method);
        return cacheConfiguration;
    }


}
