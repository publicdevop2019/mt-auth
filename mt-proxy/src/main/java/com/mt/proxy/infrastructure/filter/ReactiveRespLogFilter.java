package com.mt.proxy.infrastructure.filter;

import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;

import com.mt.proxy.domain.ReportService;
import com.mt.proxy.infrastructure.AppConstant;
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
            checkHeader(exchange);
            reportService.logResponseDetail(exchange);
            return Mono.empty();
        });
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private void checkHeader(ServerWebExchange exchange) {
        String reqUuid = exchange.getRequest().getHeaders().getFirst(
            AppConstant.REQ_UUID);
        String respUuid = exchange.getResponse().getHeaders().getFirst(
            AppConstant.REQ_UUID);
        if (reqUuid == null && respUuid == null) {
            log.warn("missing request & response uuid header, which should not happen");
        } else if (reqUuid != null && respUuid == null) {
            exchange.getResponse().getHeaders().set(
                REQ_UUID, reqUuid);
        } else if (reqUuid == null) {
            String value = exchange.getRequest().getPath().value();
            log.warn("missing request uuid header, which should not happen path = {}", value);
        } else {
            String value = exchange.getRequest().getPath().value();
            if (!reqUuid.equals(respUuid)) {
                log.warn(
                    "request & response uuid header mismatch, which should not happen path = {}, req uuid {}, resp uuid {}",
                    value, reqUuid, respUuid);
            }
        }
    }

}
