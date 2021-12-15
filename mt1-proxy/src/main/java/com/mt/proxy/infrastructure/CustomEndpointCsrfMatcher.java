package com.mt.proxy.infrastructure;

import com.mt.proxy.domain.CsrfService;
import com.mt.proxy.domain.EndpointService;
import com.mt.proxy.domain.MethodPathKey;
import com.mt.proxy.infrastructure.springcloudgateway.SCGEndpointFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class CustomEndpointCsrfMatcher implements ServerWebExchangeMatcher {
    @Autowired
    private CsrfService csrfService;
    @Autowired
    private EndpointService endpointService;

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest())
                .filter(request -> {
                    if (SCGEndpointFilter.isWebSocket(request.getHeaders())) {
                        log.debug("csrf not required for websocket");
                        return true;
                    } else {
                        log.debug("checking csrf token for {}", request.getPath().value());
                        Set<MethodPathKey> patchKeyStream = csrfService.getBypassList();
                        AntPathMatcher matcher = endpointService.getPathMater();
                        Optional<MethodPathKey> first = patchKeyStream.stream().filter(pattern -> matcher.match(pattern.getPath(), request.getPath().value())).filter(key -> key.getMethod().equalsIgnoreCase(request.getMethodValue())).findFirst();
                        if (first.isPresent()) {
                            log.debug("csrf not required");
                        } else {
                            log.debug("csrf required");
                        }
                        return first.isPresent();
                    }
                })
                .flatMap(e -> MatchResult.notMatch())
                .switchIfEmpty(MatchResult.match());
    }
}
