package com.mt.proxy.infrastructure.springcloudgateway;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import static com.mt.proxy.infrastructure.springcloudgateway.SCGETagFilter.getResponseBody;

@Slf4j
@Component
public class SCGZipFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if ("websocket".equals(request.getHeaders().getUpgrade())) {
            return chain.filter(exchange);
        }
        ServerHttpResponse decoratedResponse = zipResponse(exchange);
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return -4;
    }

    private ServerHttpResponse zipResponse(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (originalResponse.getHeaders().getContentType() != null
                        && originalResponse.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON_UTF8)) {
                    Flux<DataBuffer> flux;
                    if (body instanceof Mono) {
                        Mono<? extends DataBuffer> mono = (Mono<? extends DataBuffer>) body;
                        body = mono.flux();
                    }
                    if (body instanceof Flux) {
                        flux = (Flux<DataBuffer>) body;
                        boolean finalIsJson = true;
                        return super.writeWith(flux.buffer().map(dataBuffers -> {
                            byte[] responseBody = new byte[0];
                            try {
                                responseBody = getResponseBody(dataBuffers);
                            } catch (IOException e) {
                                log.error("error during read response", e);
                                originalResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                                return bufferFactory.wrap(responseBody);
                            }
                            boolean minLength = responseBody.length > 1024;
                            if (minLength && finalIsJson) {
                                byte[] compressed = new byte[0];
                                try {
                                    compressed = compress(responseBody);
                                } catch (IOException e) {
                                    log.error("error during compress", e);
                                }
                                log.debug("gzip response length before {} after {}", responseBody.length, compressed.length);
                                originalResponse.getHeaders().setContentLength(compressed.length);
                                originalResponse.getHeaders().set(HttpHeaders.CONTENT_ENCODING, "gzip");
                                return bufferFactory.wrap(compressed);
                            } else {
                                return bufferFactory.wrap(responseBody);
                            }
                        }));
                    }
                } else {
                    return super.writeWith(body);
                }
                return super.writeWith(body);
            }
        };
    }

    private byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }
}
