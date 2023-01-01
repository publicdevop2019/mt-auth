package com.mt.proxy.infrastructure.filter;

import static com.mt.proxy.infrastructure.AppConstant.REQ_CLIENT_IP;
import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;

import com.mt.proxy.domain.ReportService;
import com.mt.proxy.domain.Utility;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
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
public class ReactiveReqLogFilter implements WebFilter, Ordered {
    @Autowired
    ReportService reportService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //clear previous value
        MDC.put(REQ_UUID, null);
        MDC.put(REQ_CLIENT_IP, null);
        ServerHttpRequest request = exchange.getRequest();
        String uuid = request.getHeaders().getFirst(REQ_UUID);
        String ip = getClientIp(request);
        MDC.put(REQ_CLIENT_IP, ip);
        ServerHttpRequest httpRequest = null;
        if (null == uuid) {
            String s = java.util.UUID.randomUUID() + "_g";
            log.debug("uuid not found, auto generated {}", s);
            MDC.put(REQ_UUID, s);
            httpRequest = request.mutate().headers(h -> h.set(REQ_UUID, s)).build();
        } else {
            MDC.put(REQ_UUID, uuid);
        }
        beforeCommitResponse(exchange);
        if (httpRequest != null) {
            if (Utility.isWebSocket(request.getHeaders())) {
                return chain.filter(exchange.mutate().request(httpRequest).build());
            }
            reportService.logRequestDetails(exchange.getRequest());
            return chain
                .filter(exchange.mutate().request(httpRequest).build());
        } else {
            if (Utility.isWebSocket(request.getHeaders())) {
                return chain.filter(exchange);
            }
            reportService.logRequestDetails(exchange.getRequest());
            return chain.filter(exchange.mutate().build());
        }
    }

    public static String getClientIp(ServerHttpRequest request) {
        String clientIp = "NOT_FOUND";
        String first = request.getHeaders().getFirst("X-FORWARDED-FOR");
        if (first != null) {
            clientIp = first;
        } else {
            if (request.getRemoteAddress() != null) {
                clientIp = request.getRemoteAddress().toString();
            }
        }
        return clientIp;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private void beforeCommitResponse(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.beforeCommit(() -> {
            checkHeader(exchange);
            reportService.logResponseDetail(exchange.getResponse());
            return Mono.empty();
        });
    }

    private void checkHeader(ServerWebExchange exchange) {
        String reqHeader = exchange.getRequest().getHeaders().getFirst(
            com.mt.proxy.infrastructure.AppConstant.REQ_UUID);
        String respHeader = exchange.getResponse().getHeaders().getFirst(
            com.mt.proxy.infrastructure.AppConstant.REQ_UUID);
        if (respHeader == null && reqHeader != null) {
            exchange.getResponse().getHeaders().set(
                com.mt.proxy.infrastructure.AppConstant.REQ_UUID, reqHeader);
        } else {
            log.warn("missing request header, which should not happen");
        }
    }
}
