package com.mt.proxy.infrastructure.filter;

import static com.mt.proxy.domain.Utility.getUuid;
import static com.mt.proxy.infrastructure.AppConstant.REQ_CLIENT_IP;
import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;
import static com.mt.proxy.domain.Utility.getClientIp;

import com.mt.proxy.domain.ReportService;
import com.mt.proxy.infrastructure.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
        //clear previous value
        MDC.put(REQ_UUID, null);
        MDC.put(REQ_CLIENT_IP, null);
        ServerHttpRequest request = exchange.getRequest();
        String uuid = getUuid(request);
        String ip = getClientIp(request);
        MDC.put(REQ_UUID, uuid);
        MDC.put(REQ_CLIENT_IP, ip);
        exchange.getResponse().beforeCommit(() -> {
            checkHeader(exchange);
            reportService.logResponseDetail(exchange.getResponse());
            return Mono.empty();
        });
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private void checkHeader(ServerWebExchange exchange) {
        String reqHeader = exchange.getRequest().getHeaders().getFirst(
            AppConstant.REQ_UUID);
        String respHeader = exchange.getResponse().getHeaders().getFirst(
            AppConstant.REQ_UUID);
        if (respHeader == null && reqHeader != null) {
            exchange.getResponse().getHeaders().set(
                REQ_UUID, reqHeader);
        } else {
            log.warn("missing request header, which should not happen");
        }
    }

}
