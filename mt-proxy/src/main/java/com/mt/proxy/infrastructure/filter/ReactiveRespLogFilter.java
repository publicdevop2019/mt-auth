package com.mt.proxy.infrastructure.filter;

import static com.mt.proxy.domain.Utility.getUuid;
import static com.mt.proxy.infrastructure.AppConstant.REQ_CLIENT_IP;
import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;
import static com.mt.proxy.infrastructure.filter.ReactiveReqLogFilter.getClientIp;

import com.mt.proxy.domain.ReportService;
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
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }


}
