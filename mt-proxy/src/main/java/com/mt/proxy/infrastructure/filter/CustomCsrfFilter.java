package com.mt.proxy.infrastructure.filter;

import com.mt.proxy.domain.DomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
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
public class CustomCsrfFilter implements WebFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        boolean bypassCsrf = DomainRegistry.getCsrfService()
            .checkBypassCsrf(exchange.getRequest());
        if (!bypassCsrf) {
            boolean valid = DomainRegistry.getCsrfService().checkCsrfValue(exchange);
            if (!valid) {
                ServerHttpResponse response = exchange.getResponse();
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                String jsonResponse = "{\"msg\": \"csrf required\"}";
                return response.writeWith(
                    Mono.just(response.bufferFactory().wrap(jsonResponse.getBytes())));
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 2;
    }

}
