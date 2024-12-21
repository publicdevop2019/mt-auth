package com.mt.proxy.infrastructure.filter;

import com.mt.proxy.domain.DomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * this filter runs before any AbstractHandlerMapping to prevent unexpected 403 due to CORS check failed
 */
@Slf4j
@Component
public class CustomCorsFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (DomainRegistry.getCorsService().checkCors(exchange)) {
            return Mono.empty();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }

}
