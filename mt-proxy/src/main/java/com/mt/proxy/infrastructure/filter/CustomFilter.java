package com.mt.proxy.infrastructure.filter;

import static com.mt.proxy.domain.Utility.readFormData;

import com.mt.proxy.domain.CacheConfiguration;
import com.mt.proxy.domain.DomainRegistry;
import com.mt.proxy.domain.EndpointCheckResult;
import com.mt.proxy.domain.GzipService;
import com.mt.proxy.domain.SanitizeService;
import com.mt.proxy.domain.Utility;
import com.mt.proxy.domain.rate_limit.RateLimitResult;
import com.mt.proxy.infrastructure.AppConstant;
import com.mt.proxy.infrastructure.LogService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomFilter implements WebFilter, Ordered {
    private static final String PROXY_INTERNAL_ENDPOINT = "/info/checkSum";
    @Value("${mt.common.domain-name}")
    String domain;

    private static ServerHttpRequestDecorator decorateRequest(ServerWebExchange exchange,
                                                              HttpHeaders headers,
                                                              CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(headers);
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

    private static byte[] getResponseBody(List<DataBuffer> dataBuffers) throws IOException {
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

    private static boolean responseError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        LogService.reactiveLog(exchange.getRequest(),
            () -> log.trace("checking response in case of downstream error"));
        boolean b = response.getStatusCode() != null
            &&
            response.getStatusCode().is5xxServerError();
        if (b) {
            LogService.reactiveLog(exchange.getRequest(),
                () -> log.debug("downstream error, hidden error body"));
            response.getHeaders().setContentLength(0);
        }
        return b;
    }

    private void addCsrfHeader(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        if (request.getCookies().get("XSRF-TOKEN") == null
            &&
            request.getHeaders().get("x-xsrf-token") == null) {
            String var0 = UUID.randomUUID().toString();
            response.getHeaders().add(HttpHeaders.SET_COOKIE,
                "XSRF-TOKEN=" + var0 + "; SameSite=None; Path=/; Secure; Domain=" + domain);
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (proxyInternalEndpoint(exchange)) {
            LogService.reactiveLog(exchange.getRequest(),
                () -> log.debug("skip check for proxy internal endpoint"));
            return chain.filter(exchange);
        }
        if (DomainRegistry.getCorsService().checkCors(exchange)) {
            return Mono.empty();
        }
        if (!DomainRegistry.getCsrfService().checkCsrf(exchange.getRequest())) {
            ServerHttpResponse response = exchange.getResponse();
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String jsonResponse = "{\"msg\": \"csrf required\"}";
            return response.writeWith(
                Mono.just(response.bufferFactory().wrap(jsonResponse.getBytes())));
        }
        CustomFilterContext context = new CustomFilterContext();
        ServerHttpRequest request = exchange.getRequest();
        context.setWebsocket(Utility.isWebSocket(request.getHeaders()));
        context.setAuthHeader(Utility.getAuthHeader(request));
        checkEndpoint(exchange.getRequest(), context);
        if (context.hasCheckFailed()) {
            return stopResponse(exchange, context);
        }
        if (Boolean.TRUE.equals(context.getWebsocket())) {
            //for websocket only endpoint check is performed after token check
            return chain.filter(exchange);
        }
        LogService.reactiveLog(request,
            () -> log.debug("checking rate limit"));
        checkRateLimit(exchange, context);
        if (context.hasCheckFailed()) {
            LogService.reactiveLog(request,
                () -> log.debug("rate limit check failed"));
            return stopResponse(exchange, context);
        }
        LogService.reactiveLog(request,
            () -> log.debug("update request"));
        Mono<ServerHttpRequest> requestMono = updateRequest(exchange, context);
        if (context.hasCheckFailed()) {
            LogService.reactiveLog(request,
                () -> log.debug("check failed during update request"));
            return stopResponse(exchange, context);
        }
        LogService.reactiveLog(request,
            () -> log.debug("log request and response detail"));
        //only log request if pass endpoint & rate limit & token (except /oauth/token endpoint) check, so system is not impacted by malicious request
        DomainRegistry.getReportService().logRequestDetails(exchange.getRequest());
        ServerHttpResponse updatedResp = updateResponse(exchange);
        LogService.reactiveLog(request,
            () -> log.debug("response updated"));
        return requestMono.flatMap(req -> {
            if (Boolean.TRUE.equals(context.hasCheckFailed())) {
                LogService.reactiveLog(request,
                    () -> log.debug("has check failed, stop response"));
                return stopResponse(exchange, context);
            }
            if (Boolean.TRUE.equals(context.getBodyCopied())) {
                LogService.reactiveLog(request,
                    () -> log.debug("body copied"));
                return chain.filter(exchange.mutate().request(req).response(updatedResp).build());
            } else {
                LogService.reactiveLog(request,
                    () -> log.debug("body not copied"));
                return chain.filter(exchange.mutate().response(updatedResp).build());
            }
        });
    }

    private boolean proxyInternalEndpoint(ServerWebExchange exchange) {
        return PROXY_INTERNAL_ENDPOINT.equals(exchange.getRequest().getPath().value());
    }

    private Mono<Void> stopResponse(ServerWebExchange exchange, CustomFilterContext context) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(context.getHttpErrorStatus());
        DomainRegistry.getReportService().logResponseDetail(exchange);
        return response.setComplete();
    }

    private ServerHttpResponse updateResponse(ServerWebExchange exchange) {
        addCsrfHeader(exchange);
        addCacheHeader(exchange);
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                boolean b = responseError(exchange);
                if (b) {
                    return Mono.empty();
                }
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
                        //below order is important
                        byte[] sanitizedBody =
                            SanitizeService
                                .sanitizeResp(responseBody, originalResponse.getHeaders());
                        byte[] zippedBody = GzipService.updateGzip(sanitizedBody, originalResponse);
                        updateEtag(zippedBody, exchange);
                        return bufferFactory.wrap(zippedBody);
                    }));
                }
                return super.writeWith(body);
            }

        };
    }

    private void updateEtag(byte[] responseBody, ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        CacheConfiguration cacheConfiguration =
            DomainRegistry.getCacheService().getCacheConfiguration(exchange, true);
        if (cacheConfiguration != null && Boolean.TRUE.equals(cacheConfiguration.getEtag()) &&
            HttpStatus.OK.equals(exchange.getResponse().getStatusCode())) {
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
                String ifNoneMatch =
                    exchange.getRequest().getHeaders().getIfNoneMatch().get(0);
                if (ifNoneMatch.equals(etag)) {
                    ServerHttpResponse response = exchange.getResponse();
                    log.debug("etag match, return 304");
                    response.setStatusCode(HttpStatus.NOT_MODIFIED);
                }
            }
        }
    }

    private void addCacheHeader(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        if (HttpMethod.GET.equals(request.getMethod())) {
            exchange.getResponse().beforeCommit(() -> {
                CacheConfiguration cacheConfiguration =
                    DomainRegistry.getCacheService().getCacheConfiguration(exchange, false);
                if (cacheConfiguration != null) {
                    //remove existing cache header
                    exchange.getResponse().getHeaders().remove("Cache-Control");
                    exchange.getResponse().getHeaders().remove("Expires");
                    exchange.getResponse().getHeaders().remove("Pragma");
                    if (Boolean.FALSE.equals(cacheConfiguration.getAllowCache())) {
                        exchange.getResponse().getHeaders()
                            .setCacheControl("no-cache, no-store, must-revalidate");
                        exchange.getResponse().getHeaders().setPragma("no-cache");
                        exchange.getResponse().getHeaders().setExpires(0);
                    } else {
                        //allow cache
                        if (cacheConfiguration.getCacheControl() != null) {
                            String cacheControlValue = cacheConfiguration.getCacheControlValue();
                            exchange.getResponse().getHeaders().setCacheControl(cacheControlValue);
                        }
                        if (cacheConfiguration.getExpires() != null) {
                            exchange.getResponse().getHeaders()
                                .setExpires(cacheConfiguration.getExpires());
                        }
                        if (cacheConfiguration.getVary() != null) {
                            exchange.getResponse().getHeaders()
                                .setVary(List.of(cacheConfiguration.getVary()));
                        }
                    }
                }
                return Mono.empty();
            });
        }
    }

    /**
     * update request, how body is updated is same as SCG provided approach.
     * refer to org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
     *
     * @param exchange ServerWebExchange
     * @param context  CustomFilterContext
     * @return Mono<ServerHttpRequest>
     */
    private Mono<ServerHttpRequest> updateRequest(ServerWebExchange exchange,
                                                  CustomFilterContext context) {
        ServerHttpRequest request = exchange.getRequest();
        if (Utility.isTokenRequest(request)
            ||
            DomainRegistry
                .getJsonSanitizeService()
                .sanitizeRequired(request.getMethod(), request.getHeaders().getContentType())) {
            context.bodyReadRequired();
            ServerRequest serverRequest =
                ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).map(body -> {
                if (Utility.isTokenRequest(request)) {
                    Map<String, String> parameters = readFormData(body);
                    if (DomainRegistry.getRevokeTokenService()
                        .revoked(context.getAuthHeader(), request.getPath().toString(),
                            parameters)) {
                        context.tokenRevoked();
                    }
                } else {
                    String sanitize = DomainRegistry.getJsonSanitizeService().sanitizeRequest(body);
                    context.setNewContentLength(sanitize.getBytes().length);
                    return sanitize;
                }
                return body;
            });
            BodyInserter<Mono<String>, ReactiveHttpOutputMessage>
                bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            CachedBodyOutputMessage
                outputMessage = new CachedBodyOutputMessage(exchange, headers);
            Mono<Void> insert = bodyInserter.insert(outputMessage, new BodyInserterContext());
            return insert.then(Mono.defer(() -> {
                if (DomainRegistry.getJsonSanitizeService()
                    .sanitizeRequired(request.getMethod(), request.getHeaders().getContentType())) {
                    headers.setContentLength(context.getNewContentLength());
                }
                return Mono.just(decorateRequest(exchange, headers, outputMessage));
            }));
        } else {
            if (DomainRegistry.getRevokeTokenService()
                .revoked(context.getAuthHeader(), request.getPath().toString(), null)) {
                LogService.reactiveLog(request,
                    () -> log.debug("token revoked"));
                context.tokenRevoked();
                return Mono.just(request);
            }
        }
        return Mono.just(request);
    }

    private void checkEndpoint(ServerHttpRequest request, CustomFilterContext context) {
        LogService.reactiveLog(request,
            () -> log.trace("endpoint path: {} scheme: {}", request.getURI().getPath(),
                request.getURI().getScheme()));
        EndpointCheckResult result = DomainRegistry.getEndpointService()
            .checkAccess(request, context.getAuthHeader(), context.getWebsocket());
        LogService.reactiveLog(request,
            () -> log.debug("check result is {}, reason {}", result.isPassed(),
                result.getReason()));
        if (!result.isPassed()) {
            context.endpointCheckFailed(result.getReason().getHttpStatus());
        }
    }

    private void checkRateLimit(ServerWebExchange exchange, CustomFilterContext context) {
        ServerHttpResponse response = exchange.getResponse();
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethodValue();
        RateLimitResult rateLimitResult = DomainRegistry.getRateLimitService()
            .withinRateLimit(path, method,
                exchange.getRequest().getHeaders(), exchange.getRequest().getRemoteAddress());
        if (rateLimitResult.getAllowed() == null || !rateLimitResult.getAllowed()) {
            response.getHeaders()
                .set(AppConstant.X_RATE_LIMIT, "0");
            context.rateLimitReached();
        }
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.beforeCommit(() -> {
            originalResponse.getHeaders()
                .set(AppConstant.X_RATE_LIMIT, String.valueOf(rateLimitResult.getNewTokens()));
            return Mono.empty();
        });
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 3;
    }

}