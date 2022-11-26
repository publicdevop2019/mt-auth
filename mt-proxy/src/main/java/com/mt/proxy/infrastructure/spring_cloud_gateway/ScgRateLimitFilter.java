package com.mt.proxy.infrastructure.spring_cloud_gateway;

import com.mt.proxy.domain.DomainRegistry;
import com.mt.proxy.domain.rate_limit.RateLimitResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ScgRateLimitFilter implements GlobalFilter, Ordered {

    private static final String X_RATE_LIMIT = "x-mt-ratelimit-left";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethodValue();
        RateLimitResult rateLimitResult = DomainRegistry.getRateLimitService()
            .withinRateLimit(path, method,
                exchange.getRequest().getHeaders());
        if (!rateLimitResult.getAllowed()) {
            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            response.getHeaders()
                .set(X_RATE_LIMIT, "0");
            return response.setComplete();
        }
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.beforeCommit(() -> {
            originalResponse.getHeaders()
                .set(X_RATE_LIMIT, String.valueOf(rateLimitResult.getNewTokens()));
            return Mono.empty();
        });

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }
}
