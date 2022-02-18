package com.mt.proxy.infrastructure.springcloudgateway;

import com.google.json.JsonSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.mt.proxy.infrastructure.springcloudgateway.SCGETagFilter.getResponseBody;


@Slf4j
@Component
public class SCGResponseJsonSanitizerFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if ("websocket".equals(request.getHeaders().getUpgrade())) {
            return chain.filter(exchange);
        }
        ServerHttpResponse decoratedResponse = responseJsonSanitizer(exchange);
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private ServerHttpResponse responseJsonSanitizer(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpHeaders headers = originalResponse.getHeaders();
                if (MediaType.APPLICATION_JSON_UTF8.equals(headers.getContentType())) {
                    Flux<DataBuffer> flux;
                    if (body instanceof Mono) {
                        Mono<? extends DataBuffer> mono = (Mono<? extends DataBuffer>) body;
                        body = mono.flux();
                    }
                    if (body instanceof Flux) {
                        flux = (Flux<DataBuffer>) body;
                        return super.writeWith(flux.buffer().map(dataBuffers -> {
                            byte[] responseBody = new byte[0];
                            try {
                                responseBody = getResponseBody(dataBuffers);
                            } catch (IOException e) {
                                log.error("error during read response", e);
                                originalResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                                return bufferFactory.wrap(responseBody);
                            }
                            String responseBodyString = new String(responseBody, StandardCharsets.UTF_8);
                            String afterSanitize = JsonSanitizer.sanitize(responseBodyString);
                            byte[] bytes = afterSanitize.getBytes(StandardCharsets.UTF_8);
                            if (headers.getContentLength() != afterSanitize.getBytes(StandardCharsets.UTF_8).length)
                                log.debug("sanitized response length before {} after {}", responseBody.length, bytes.length);
                            headers.setContentLength(bytes.length);
                            return bufferFactory.wrap(bytes);
                        }));
                    }
                }
                return super.writeWith(body);
            }
        };
    }
}
