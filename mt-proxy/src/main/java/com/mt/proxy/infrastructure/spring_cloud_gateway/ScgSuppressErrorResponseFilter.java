package com.mt.proxy.infrastructure.spring_cloud_gateway;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ScgSuppressErrorResponseFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.trace("inside ScgSuppressErrorResponseFilter - order -3");
        ServerHttpRequest request = exchange.getRequest();
        if ("websocket".equals(request.getHeaders().getUpgrade())) {
            return chain.filter(exchange);
        }
        ServerHttpResponse decoratedResponse = errorResponseDecorator(exchange);
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return -3;
    }

    private ServerHttpResponse errorResponseDecorator(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                log.trace("inside [writeWith] handler");
                log.debug("checking response in case of downstream error");
                if (originalResponse.getStatusCode() != null
                    &&
                    originalResponse.getStatusCode().is5xxServerError()) {
                    originalResponse.getHeaders().setContentLength(0);
                    return Mono.empty();
                }
                return super.writeWith(body);
            }
        };
    }
}
