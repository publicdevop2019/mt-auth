package com.mt.proxy.infrastructure.filter;

import static com.mt.proxy.infrastructure.AppConstant.TRACE_ID_HTTP;
import static com.mt.proxy.infrastructure.AppConstant.X_TRACE_ID;

import com.mt.proxy.domain.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
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
public class ReactiveRespLogFilter implements WebFilter, Ordered {
    @Autowired
    private ReportService reportService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().beforeCommit(() -> {
            addHeaders(exchange);
            reportService.logResponseDetail(exchange);
            return Mono.empty();
        });
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private void addHeaders(ServerWebExchange exchange) {
        String traceId = exchange.getRequest().getHeaders().getFirst(
            TRACE_ID_HTTP);
        exchange.getResponse().getHeaders().set(
            X_TRACE_ID, traceId);
    }

}
