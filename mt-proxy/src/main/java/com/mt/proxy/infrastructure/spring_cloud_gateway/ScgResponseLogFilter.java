package com.mt.proxy.infrastructure.spring_cloud_gateway;

import static com.mt.proxy.infrastructure.AppConstant.REQ_CLIENT_IP;
import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;

import com.mt.proxy.domain.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * scg filter helps to update log uuid, client ip after response received.
 */
@Slf4j
@Component
public class ScgResponseLogFilter implements GlobalFilter, Ordered {


    @Autowired
    ReportService reportService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Void> responseLogUpdater = Mono.fromRunnable(() -> {
            //clear previous value
            MDC.put(REQ_UUID, null);
            ServerHttpRequest request = exchange.getRequest();
            String uuidHeader = request.getHeaders().getFirst(REQ_UUID);
            //response header must have uuid
            MDC.put(REQ_UUID, uuidHeader);

            String clientIpHeader = request.getHeaders().getFirst("X-FORWARDED-FOR");
            if (clientIpHeader != null) {
                MDC.put(REQ_CLIENT_IP, clientIpHeader);
            } else {
                if (request.getRemoteAddress() != null) {
                    MDC.put(REQ_CLIENT_IP, request.getRemoteAddress().toString());
                } else {
                    MDC.put(REQ_CLIENT_IP, "NOT_FOUND");
                }
            }
            log.debug("checking response log value");
            reportService.logResponseDetail(exchange.getResponse(),exchange.getRequest());
        });
        return chain.filter(exchange).then(responseLogUpdater);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}

