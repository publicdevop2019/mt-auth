package com.mt.proxy.infrastructure.springcloudgateway;

import com.google.json.JsonSanitizer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.mt.proxy.infrastructure.springcloudgateway.SCGRevokeTokenFilter.decorate;

@Slf4j
@Component
public class SCGRequestJsonSanitizerFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (request.getMethod() != null && !request.getMethod().equals(HttpMethod.GET) && !request.getMethod().equals(HttpMethod.OPTIONS) && request.getHeaders().getContentType() != null && request.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON)) {
            FilterContext filterContext = new FilterContext();
            Mono<String> sanitizedBody = readJsonFromRequest(exchange, filterContext);
            BodyInserter bodyInserter = BodyInserters.fromPublisher(sanitizedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            SCGRevokeTokenFilter.CachedBodyOutputMessage outputMessage = new SCGRevokeTokenFilter.CachedBodyOutputMessage(exchange, headers);
            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                headers.setContentLength(filterContext.contentLength);
                ServerHttpRequest decorator = decorate(exchange, headers, outputMessage);
                return chain.filter(exchange.mutate().request(decorator).build());
            }));
        } else {
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }

    private Mono<String> readJsonFromRequest(ServerWebExchange exchange, FilterContext filterContext) {
        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        return serverRequest.bodyToMono(String.class).map(e -> {
            String sanitize = JsonSanitizer.sanitize(e);
            if (e.getBytes().length != sanitize.getBytes().length) {
                log.debug("sanitized request length before {} after {}", e.getBytes().length, sanitize.getBytes().length);
            }
            filterContext.contentLength = sanitize.getBytes().length;
            return sanitize;
        });
    }

    @Data
    public static class FilterContext {
        private int contentLength = 0;
    }

}
