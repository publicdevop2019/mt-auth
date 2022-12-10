package com.mt.proxy.infrastructure;

import com.mt.proxy.domain.DomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomEndpointCsrfMatcher implements ServerWebExchangeMatcher {
    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest())
            .filter(request ->
                DomainRegistry.getCsrfService()
                    .checkBypassCsrf(request.getPath().value(),request.getMethodValue(),request.getHeaders()))
            .flatMap(e -> MatchResult.notMatch())
            .switchIfEmpty(MatchResult.match());
    }
}
