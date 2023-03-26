package com.mt.proxy.infrastructure.filter;

import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;

import com.mt.proxy.domain.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * log filter which work both internal & external api calls.
 * note: scg filter will not work for internal api calls
 */
@Slf4j
@Component
public class ReactiveReqLogFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String uuid = Utility.getUuid(request);
        ServerHttpRequest httpRequest = null;
        if (null == uuid) {
            String newUuid = java.util.UUID.randomUUID() + "_g";
            log.debug("uuid not found, auto generated {} for endpoint {}", newUuid,
                request.getPath().value());
            httpRequest = request.mutate().headers(h -> h.set(REQ_UUID, newUuid)).build();
        }
        if (httpRequest != null) {
            return chain
                .filter(exchange.mutate().request(httpRequest).build());
        } else {
            return chain.filter(exchange.mutate().build());
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
