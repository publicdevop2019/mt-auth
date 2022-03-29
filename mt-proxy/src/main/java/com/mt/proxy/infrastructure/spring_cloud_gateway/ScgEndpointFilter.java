package com.mt.proxy.infrastructure.spring_cloud_gateway;

import com.mt.proxy.domain.DomainRegistry;
import java.text.ParseException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ScgEndpointFilter implements GlobalFilter, Ordered {
    public static boolean isWebSocket(HttpHeaders headers) {
        log.debug("upgrade header value is {}", headers.getUpgrade());
        log.trace("all header value is {}", headers);
        return "websocket".equals(headers.getUpgrade());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("start of endpoint filter");
        String authHeader = null;
        ServerHttpRequest request = exchange.getRequest();
        log.debug("endpoint path: {} scheme: {}", exchange.getRequest().getURI().getPath(),
            exchange.getRequest().getURI().getScheme());
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        List<String> temp;
        boolean webSocket = false;
        if (isWebSocket(headers)) {
            log.debug("current request is websocket");
            webSocket = true;
            temp = request.getQueryParams().get("jwt");
            if (temp != null && !temp.isEmpty()) {
                authHeader = "Bearer " + new String(Base64.decode(temp.get(0)));
            }
        } else {
            log.debug("current request is not websocket");
            temp = headers.get("authorization");
            if (temp != null && !temp.isEmpty()) {
                authHeader = temp.get(0);
            }
        }
        boolean allow;
        try {
            //noinspection ConstantConditions
            allow = DomainRegistry.getEndpointService().checkAccess(
                request.getPath().toString(),
                request.getMethod().name(),
                authHeader, webSocket);
        } catch (ParseException e) {
            log.error("error during parse", e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.setComplete();
        }
        if (!allow) {
            log.debug("access is not allowed");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        log.debug("access is allowed");
        log.debug("end of endpoint filter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
