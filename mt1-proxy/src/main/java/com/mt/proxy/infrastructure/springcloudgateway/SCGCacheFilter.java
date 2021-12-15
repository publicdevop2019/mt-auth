package com.mt.proxy.infrastructure.springcloudgateway;

import com.mt.proxy.domain.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.mt.proxy.infrastructure.springcloudgateway.SCGETagFilter.updateResponseWithETag;

/**
 * manage api level cache control for http get request
 */
@Slf4j
@Component
public class SCGCacheFilter implements GlobalFilter, Ordered {
    @Autowired
    CacheService cacheService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Mono<Void> responseCacheHandler = Mono.fromRunnable(() -> {
            CacheService.CacheConfiguration cacheConfiguration = cacheService.getCacheConfiguration(exchange,false);
            if (cacheConfiguration != null) {
                //remove existing cache header
                exchange.getResponse().getHeaders().remove("Cache-Control");
                exchange.getResponse().getHeaders().remove("Expires");
                exchange.getResponse().getHeaders().remove("Pragma");
                if (!cacheConfiguration.isAllowCache()) {
                    exchange.getResponse().getHeaders().setCacheControl("no-cache, no-store, must-revalidate");
                    exchange.getResponse().getHeaders().setPragma("no-cache");
                    exchange.getResponse().getHeaders().setExpires(0);
                } else {
                    //allow cache
                    if (cacheConfiguration.getCacheControl() != null) {
                        String cacheControlValue = cacheConfiguration.getCacheControlValue();
                        exchange.getResponse().getHeaders().setCacheControl(cacheControlValue);
                    }
                    if (cacheConfiguration.getExpires() != null) {
                        exchange.getResponse().getHeaders().setExpires(cacheConfiguration.getExpires());
                    }
                    if (cacheConfiguration.getVary() != null) {
                        exchange.getResponse().getHeaders().setVary(List.of(cacheConfiguration.getVary()));
                    }
                }
            }
        });
        if (!SCGEndpointFilter.isWebSocket(request.getHeaders()) && HttpMethod.GET.equals(request.getMethod())) {
            return chain.filter(exchange).then(responseCacheHandler);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;//required
    }
}
