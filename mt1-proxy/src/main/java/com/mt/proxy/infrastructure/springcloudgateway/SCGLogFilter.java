package com.mt.proxy.infrastructure.springcloudgateway;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class SCGLogFilter implements GlobalFilter, Ordered {

    public static final String REQ_UUID = "UUID";
    public static final String REQ_CLIENT_IP = "CLIENT_IP";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        MDC.put(REQ_UUID, null);//clear previous value
        ServerHttpRequest request = exchange.getRequest();
        List<String> uuidHeader = request.getHeaders().get(REQ_UUID);
        List<String> clientIpHeader = request.getHeaders().get("X-FORWARDED-FOR");
        String uuid = null;
        if (uuidHeader != null && !uuidHeader.isEmpty()) {
            uuid = uuidHeader.get(0);
        }
        String clientIp = null;
        if (clientIpHeader != null && !clientIpHeader.isEmpty()) {
            clientIp = clientIpHeader.get(0);
        }
        if (clientIp != null && clientIp.length() > 0) {
            MDC.put(REQ_CLIENT_IP, clientIp);
        } else {
            if (request.getRemoteAddress() != null) {
                MDC.put(REQ_CLIENT_IP, request.getRemoteAddress().toString());
            } else {
                MDC.put(REQ_CLIENT_IP, "NOT_FOUND");
            }
        }
        ServerHttpRequest httpRequest = null;
        if (null == uuid) {
            String s = java.util.UUID.randomUUID() + "_g";
            log.debug("uuid not found, auto generated {}", s);
            MDC.put(REQ_UUID, s);
            httpRequest = request.mutate().headers(h -> h.set(REQ_UUID, s)).build();
        } else {
            MDC.put(REQ_UUID, uuid);
        }
        ServerHttpResponse decoratedResponse = uuidResponseDecorator(exchange);
        if (httpRequest != null) {
            if ("websocket".equals(request.getHeaders().getUpgrade())) {
                return chain.filter(exchange.mutate().request(httpRequest).build());
            }
            return chain.filter(exchange.mutate().request(httpRequest).response(decoratedResponse).build());
        } else {
            if ("websocket".equals(request.getHeaders().getUpgrade())) {
                return chain.filter(exchange);
            }
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        }
    }

    @Override
    public int getOrder() {
        return -99;
    }

    private ServerHttpResponse uuidResponseDecorator(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        List<String> uuidHeader = exchange.getRequest().getHeaders().get(REQ_UUID);
        if (uuidHeader != null && !uuidHeader.isEmpty()) {
            originalResponse.getHeaders().set(REQ_UUID, uuidHeader.get(0));
        }
        return originalResponse;
    }
}
