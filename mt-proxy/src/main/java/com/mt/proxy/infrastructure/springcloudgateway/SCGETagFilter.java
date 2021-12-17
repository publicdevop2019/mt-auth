package com.mt.proxy.infrastructure.springcloudgateway;

import com.mt.proxy.domain.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * manage api level cache control for http get request
 */
@Slf4j
@Component
public class SCGETagFilter implements GlobalFilter, Ordered {
    @Autowired
    CacheService cacheService;

    public static byte[] getResponseBody(List<DataBuffer> dataBuffers) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            dataBuffers.forEach(i -> {
                byte[] array = new byte[i.readableByteCount()];
                i.read(array);
                DataBufferUtils.release(i);
                outputStream.write(array, 0, array.length);
            });
            return outputStream.toByteArray();
        }
    }

    public static ServerHttpResponse updateResponseWithETag(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

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
                        if (HttpStatus.OK.equals(exchange.getResponse().getStatusCode())) {
                            // length of W/ + " + 0 + 32bits md5 hash + "
                            StringBuilder builder = new StringBuilder(37);
                            builder.append("W/");
                            builder.append("\"0");
                            DigestUtils.appendMd5DigestAsHex(responseBody, builder);
                            builder.append('"');
                            String etag = builder.toString();
                            if (exchange.getRequest().getHeaders().getIfNoneMatch().isEmpty()) {
                                originalResponse.getHeaders().setETag(etag);
                                log.debug("response etag generated {}", etag);
                            } else {
                                String ifNoneMatch = exchange.getRequest().getHeaders().getIfNoneMatch().get(0);
                                if (ifNoneMatch.equals(etag)) {
                                    ServerHttpResponse response = exchange.getResponse();
                                    log.debug("etag match, return 304");
                                    response.setStatusCode(HttpStatus.NOT_MODIFIED);
                                }
                            }
                        }
                        return bufferFactory.wrap(responseBody);
                    }));
                }
                return super.writeWith(body);
            }
        };
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (!SCGEndpointFilter.isWebSocket(request.getHeaders()) && HttpMethod.GET.equals(request.getMethod())) {
            //check etag first
            log.debug("start of etag filter");
            CacheService.CacheConfiguration cacheConfiguration = cacheService.getCacheConfiguration(exchange,true);
            if (cacheConfiguration != null) {
                ServerHttpResponse decoratedResponse = updateResponseWithETag(exchange);
                if (cacheConfiguration.isEtag()) {
                    return chain.filter(exchange.mutate().response(decoratedResponse).build());
                }
            }


        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -2;//required
    }
}
