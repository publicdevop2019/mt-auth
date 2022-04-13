package com.mt.proxy.infrastructure.spring_cloud_gateway;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * scg filter helps to update log uuid after response received.
 */
@Slf4j
@Component
public class ScgResponseLogFilter implements GlobalFilter, Ordered {

    public static final String REQ_UUID = "UUID";
    public static final String REQ_CLIENT_IP = "CLIENT_IP";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Void> responseLogUpdater = Mono.fromRunnable(() -> {
            //clear previous value
            MDC.put(REQ_UUID, null);
            ServerHttpRequest request = exchange.getRequest();
            String uuidHeader = request.getHeaders().getFirst(REQ_UUID);
            String clientIpHeader = request.getHeaders().getFirst("X-FORWARDED-FOR");
            if (clientIpHeader != null) {
                MDC.put(REQ_CLIENT_IP, clientIpHeader);
            } else {
                if (request.getRemoteAddress() != null) {
                    MDC.put(REQ_CLIENT_IP, request.getRemoteAddress().toString());
                } else {
                    MDC.put(REQ_CLIENT_IP, "NOT_FOUND");
                }
            }
            MDC.put(REQ_UUID, uuidHeader);
            log.debug("checking response log value");
        });
        return chain.filter(exchange).then(responseLogUpdater);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}

