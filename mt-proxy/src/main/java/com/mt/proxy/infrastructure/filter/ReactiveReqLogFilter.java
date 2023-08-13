package com.mt.proxy.infrastructure.filter;

import static com.mt.proxy.infrastructure.AppConstant.SPAN_ID_HTTP;
import static com.mt.proxy.infrastructure.AppConstant.TRACE_ID_HTTP;

import com.mt.proxy.domain.UniqueIdGeneratorService;
import com.mt.proxy.domain.Utility;
import com.mt.proxy.infrastructure.LogService;
import lombok.extern.slf4j.Slf4j;
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
public class ReactiveReqLogFilter implements WebFilter, Ordered {
    @Autowired
    UniqueIdGeneratorService idGeneratorService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //always create new span id first
        String newTraceId;
        String newSpanId = idGeneratorService.idString();
        request = request.mutate().headers(h -> h.set(SPAN_ID_HTTP, newSpanId)).build();
        if (request.getPath().value().equalsIgnoreCase("/info/checkSum")) {
            //allow trace id to be logged for internal api calls
            newTraceId = Utility.getTraceId(request);
            if (newTraceId == null) {
                newTraceId = idGeneratorService.idString();
            }
        } else {
            //for other api calls, always create new trace id
            newTraceId = idGeneratorService.idString();
        }
        String finalNewTraceId = newTraceId;
        request = request.mutate().headers(h -> h.set(TRACE_ID_HTTP, finalNewTraceId)).build();
        String requestId = Utility.getRequestId(request);
        if (requestId == null) {
            requestId = idGeneratorService.idString() + "_g";
            String finalRequestId = requestId;
            ServerHttpRequest finalRequest = request;
            LogService.reactiveLog(exchange.getRequest(),
                () -> {
                    log.debug("created request id {} for endpoint {}",
                        finalRequestId,
                        finalRequest.getPath().value());
                });
        } else {
            String finalRequestId = requestId;
            ServerHttpRequest finalRequest = request;
            LogService.reactiveLog(exchange.getRequest(),
                () -> {
                    log.debug("received request id {} for endpoint {}",
                        finalRequestId,
                        finalRequest.getPath().value());
                });
        }
        return chain
            .filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
