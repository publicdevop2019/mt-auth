package com.mt.proxy.infrastructure.filter;

import static com.mt.proxy.domain.Utility.isWebSocket;

import com.google.json.JsonSanitizer;
import com.mt.proxy.domain.CacheConfiguration;
import com.mt.proxy.domain.CacheService;
import com.mt.proxy.domain.DomainRegistry;
import com.mt.proxy.domain.JsonSanitizeService;
import com.mt.proxy.domain.rate_limit.RateLimitResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.bouncycastle.util.encoders.Base64;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ScgCustomFilter implements GlobalFilter, Ordered {
    private static final String X_RATE_LIMIT = "x-mt-ratelimit-left";
    @Autowired
    JsonSanitizeService jsonSanitizeService;
    @Value("${manytree.domain-name}")
    String domain;
    @Autowired
    CacheService cacheService;

    public static ServerHttpRequestDecorator decorateRequest(ServerWebExchange exchange,
                                                             HttpHeaders headers,
                                                             CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(headers);
                return httpHeaders;
            }

            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

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

    public static ServerHttpResponse updateResponseWithEtag(ServerWebExchange exchange) {
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
                                String ifNoneMatch =
                                    exchange.getRequest().getHeaders().getIfNoneMatch().get(0);
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

    /**
     * zip response, this requires to read response to know if needed or not
     *
     * @param exchange ServerWebExchange
     * @return ServerHttpResponse
     */
    public static ServerHttpResponse checkZipResponse(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (originalResponse.getHeaders().getContentType() != null
                    && originalResponse.getHeaders().getContentType()
                    .equals(MediaType.APPLICATION_JSON_UTF8)) {
                    Flux<DataBuffer> flux;
                    if (body instanceof Mono) {
                        Mono<? extends DataBuffer> mono = (Mono<? extends DataBuffer>) body;
                        body = mono.flux();
                    }
                    if (body instanceof Flux) {
                        flux = (Flux<DataBuffer>) body;
                        boolean finalIsJson = true;
                        return super.writeWith(flux.buffer().map(dataBuffers -> {
                            log.trace("inside flushing");
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
                                log.debug("gzip response length before {} after {}",
                                    responseBody.length, compressed.length);
                                originalResponse.getHeaders().setContentLength(compressed.length);
                                originalResponse.getHeaders()
                                    .set(HttpHeaders.CONTENT_ENCODING, "gzip");
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

    private static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        CustomFilterContext context = new CustomFilterContext();
        ServerHttpRequest request = exchange.getRequest();
        if ("websocket".equals(request.getHeaders().getUpgrade())) {
            context.isWebsocket = true;
        }
        ServerHttpResponse response = exchange.getResponse();
        checkEndpoint(exchange, context);
        if (context.hasCheckFailed()) {
            response.setStatusCode(context.httpErrorStatus);
            return response.setComplete();
        }
        checkRateLimit(exchange, context);
        if (context.hasCheckFailed()) {
            response.setStatusCode(context.httpErrorStatus);
            return response.setComplete();
        }
        Mono<ServerHttpRequest> requestMono =
            checkRevokeToken(exchange, context);
        if (context.hasCheckFailed()) {
            response.setStatusCode(context.httpErrorStatus);
            return response.setComplete();
        }
        ServerHttpResponse csrfResp = responseDecorator(exchange);
        checkCacheHeaderConfig(exchange);
        ServerHttpResponse etagResp = checkEtagConfig(exchange, context);
        //update response decorator so it will not get overwritten, if Mono<ServerHttpResponse>, maybe this will not happen
        ServerWebExchange newExchange = exchange.mutate().response(etagResp).build();

        ServerHttpResponse zipResp = checkZipResponse(newExchange);
        //update response decorator so it will not get overwritten, if Mono<ServerHttpResponse>, maybe this will not happen
        ServerWebExchange newExchange2 = newExchange.mutate().response(zipResp).build();

        ServerHttpResponse sanitizeResp = responseJsonSanitizer(newExchange2);
        //update response decorator so it will not get overwritten, if Mono<ServerHttpResponse>, maybe this will not happen
        ServerWebExchange newExchange3 = newExchange.mutate().response(sanitizeResp).build();

        ServerHttpResponse errorResp = errorResponseDecorator(newExchange3);


        Mono<ServerHttpRequest> sanitizedRequestMono = sanitizeRequest(newExchange3, context);


        return requestMono.flatMap(req -> {
            if (context.hasCheckFailed()) {
                response.setStatusCode(context.httpErrorStatus);
                return response.setComplete();
            }
            return sanitizedRequestMono.flatMap(sanitizedReq -> {

                if (context.requestCopiedRevokeToken && context.requestCopiedSanitize) {
                    if (context.etagRequired) {
                        return chain.filter(exchange.mutate().request(req).request(sanitizedReq)
                            .response(csrfResp).response(etagResp).response(zipResp)
                            .response(sanitizeResp).response(errorResp).build());
                    }
                    return chain.filter(exchange.mutate().request(req).request(sanitizedReq)
                        .response(csrfResp).response(zipResp).response(sanitizeResp).response(errorResp).build());
                } else if (context.requestCopiedRevokeToken) {
                    if (context.etagRequired) {
                        return chain
                            .filter(
                                exchange.mutate().request(req).response(csrfResp)
                                    .response(etagResp).response(zipResp).response(sanitizeResp).response(errorResp)
                                    .build());


                    }
                    return chain
                        .filter(exchange.mutate().request(req).response(csrfResp).response(zipResp)
                            .response(sanitizeResp).response(errorResp)
                            .build());
                } else if (context.requestCopiedSanitize) {
                    if (context.etagRequired) {
                        return chain.filter(
                            exchange.mutate().request(sanitizedReq).response(csrfResp)
                                .response(etagResp).response(zipResp).response(sanitizeResp).response(errorResp)
                                .build());
                    }
                    return chain.filter(
                        exchange.mutate().request(sanitizedReq).response(csrfResp).response(zipResp)
                            .response(sanitizeResp).response(errorResp)
                            .build());
                }
                if (context.etagRequired) {
                    return chain
                        .filter(exchange.mutate().response(csrfResp).response(etagResp)
                            .response(zipResp).response(sanitizeResp).response(errorResp)
                            .build());
                }
                return chain.filter(
                    exchange.mutate().response(csrfResp).response(zipResp).response(sanitizeResp).response(errorResp)
                        .build());
            });
        });
    }
    private ServerHttpResponse errorResponseDecorator(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                log.trace("inside [writeWith] handler");
                log.debug("checking response in case of downstream error");
                if (originalResponse.getStatusCode() != null
                    &&
                    originalResponse.getStatusCode().is5xxServerError()) {
                    originalResponse.getHeaders().setContentLength(0);
                    return Mono.empty();
                }
                return super.writeWith(body);
            }
        };
    }

    private ServerHttpResponse responseJsonSanitizer(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                log.trace("inside [writeWith] handler");
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
                            log.trace("inside flushing");
                            byte[] responseBody = new byte[0];
                            try {
                                responseBody = getResponseBody(dataBuffers);
                            } catch (IOException e) {
                                log.error("error during read response", e);
                                originalResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                                return bufferFactory.wrap(responseBody);
                            }
                            String responseBodyString =
                                new String(responseBody, StandardCharsets.UTF_8);
                            String afterSanitize = JsonSanitizer.sanitize(responseBodyString);
                            byte[] bytes = afterSanitize.getBytes(StandardCharsets.UTF_8);
                            if (headers.getContentLength()
                                !=
                                afterSanitize.getBytes(StandardCharsets.UTF_8).length) {
                                log.debug("sanitized response length before {} after {}",
                                    responseBody.length, bytes.length);
                            }
                            headers.setContentLength(bytes.length);
                            return bufferFactory.wrap(bytes);
                        }));
                    }
                }
                return super.writeWith(body);
            }
        };
    }

    private ServerHttpResponse checkEtagConfig(ServerWebExchange exchange,
                                               CustomFilterContext context) {
        ServerHttpRequest request = exchange.getRequest();
        if (!isWebSocket(request.getHeaders())
            &&
            HttpMethod.GET.equals(request.getMethod())) {
            //check etag first
            log.debug("start of etag filter");
            CacheConfiguration cacheConfiguration =
                cacheService.getCacheConfiguration(exchange, true);
            if (cacheConfiguration != null) {
                ServerHttpResponse decoratedResponse = updateResponseWithEtag(exchange);
                if (cacheConfiguration.isEtag()) {
                    context.etagGenerated();
                    return decoratedResponse;
                }
            }
        }
        return exchange.getResponse();
    }

    private void checkCacheHeaderConfig(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        if (!isWebSocket(request.getHeaders())
            &&
            HttpMethod.GET.equals(request.getMethod())) {
            exchange.getResponse().beforeCommit(() -> {
                CacheConfiguration cacheConfiguration =
                    cacheService.getCacheConfiguration(exchange, false);
                if (cacheConfiguration != null) {
                    //remove existing cache header
                    exchange.getResponse().getHeaders().remove("Cache-Control");
                    exchange.getResponse().getHeaders().remove("Expires");
                    exchange.getResponse().getHeaders().remove("Pragma");
                    if (!cacheConfiguration.isAllowCache()) {
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
     * decorate response before send request to target service, note that it happens before getting actual response
     *
     * @param exchange ServerWebExchange
     * @return decorated response
     */
    private ServerHttpResponse responseDecorator(ServerWebExchange exchange) {
        if (exchange.getRequest().getCookies().get("XSRF-TOKEN") == null
            &&
            exchange.getRequest().getHeaders().get("x-xsrf-token") == null) {
            String var0 = UUID.randomUUID().toString();
            exchange.getResponse().getHeaders().add(HttpHeaders.SET_COOKIE,
                "XSRF-TOKEN=" + var0 + "; SameSite=None; Path=/; Secure; Domain=" + domain);
        }
        return exchange.getResponse();
    }


    private Mono<ServerHttpRequest> sanitizeRequest(ServerWebExchange exchange,
                                                    CustomFilterContext context) {
        ServerHttpRequest request = exchange.getRequest();
        if (jsonSanitizeService
            .sanitizeRequired(request.getMethod(), request.getHeaders().getContentType())) {

            Mono<String> sanitizedBody = sanitizeRequestBody(exchange, context);

            BodyInserter<Mono<String>, ReactiveHttpOutputMessage>
                bodyInserter = BodyInserters.fromPublisher(sanitizedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            CachedBodyOutputMessage outputMessage =
                new CachedBodyOutputMessage(exchange, headers);
            context.requestCopiedForSanitize();
            return bodyInserter.insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    headers.setContentLength(context.sanitizedContentLength);
                    ServerHttpRequest decorator = decorateRequest(exchange, headers, outputMessage);
                    return Mono.just(decorator);
                }));
        } else {
            return Mono.just(exchange.getRequest());
        }
    }

    private Mono<ServerHttpRequest> checkRevokeToken(ServerWebExchange exchange,
                                                     CustomFilterContext context) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = null;
        List<String> authorization = request.getHeaders().get("authorization");
        if (authorization != null && !authorization.isEmpty()) {
            authHeader = authorization.get(0);
        }
        //due to netty performance issue
        if (request.getPath().toString().contains("/oauth/token")) {
            log.debug("checking revoke token");
            Mono<String> modifiedBody =
                readFormDataFromRequest(exchange, authHeader, request.getPath().toString(),
                    context);
            BodyInserter<Mono<String>, ReactiveHttpOutputMessage>
                bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            CachedBodyOutputMessage
                outputMessage = new CachedBodyOutputMessage(exchange, headers);
            Mono<Void> insert = bodyInserter.insert(outputMessage, new BodyInserterContext());
            context.requestCopiedForRevokeToken();
            return insert.then(Mono.just(decorateRequest(exchange, headers, outputMessage)));
        } else {
            try {
                if (!DomainRegistry.getRevokeTokenService()
                    .checkAccess(authHeader, request.getPath().toString(), null)) {
                    context.tokenRevoked();
                    return Mono.just(request);
                }
            } catch (ParseException e) {
                log.error("error during parse", e);
                context.tokenCheckError();
                return Mono.just(request);
            }
        }
        return Mono.just(request);
    }

    private void checkEndpoint(ServerWebExchange exchange, CustomFilterContext context) {
        log.debug("start of endpoint filter");
        String authHeader = null;
        ServerHttpRequest request = exchange.getRequest();
        log.debug("endpoint path: {} scheme: {}", exchange.getRequest().getURI().getPath(),
            exchange.getRequest().getURI().getScheme());
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        List<String> temp;
        boolean webSocket = false;
        if (isWebSocket(headers)) {
            log.debug("current request is websocket");
            webSocket = true;
            temp = request.getQueryParams().get("jwt");
            if (temp != null && !temp.isEmpty()) {
                authHeader = "Bearer " + new String(Base64.decode(temp.get(0)));
            }
        } else {
            log.debug("current request is not websocket");
            temp = headers.get("authorization");
            if (temp != null && !temp.isEmpty()) {
                authHeader = temp.get(0);
            }
        }
        boolean allow;
        try {
            //noinspection ConstantConditions
            allow = DomainRegistry.getEndpointService().checkAccess(
                request.getPath().toString(),
                request.getMethod().name(),
                authHeader, webSocket);
        } catch (ParseException e) {
            log.error("error during parse", e);
            context.endpointCheckError();
            return;
        }
        if (!allow) {
            log.debug("access is not allowed");
            context.endpointCheckFailed();
            return;
        }
        log.debug("access is allowed");
        log.debug("end of endpoint filter");
        context.endpointCheckSuccess();
    }

    private Result<Void> checkRateLimit(ServerWebExchange exchange, CustomFilterContext context) {
        ServerHttpResponse response = exchange.getResponse();
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethodValue();
        RateLimitResult rateLimitResult = DomainRegistry.getRateLimitService()
            .withinRateLimit(path, method,
                exchange.getRequest().getHeaders());
        if (rateLimitResult.getAllowed() == null || !rateLimitResult.getAllowed()) {
            response.getHeaders()
                .set(X_RATE_LIMIT, "0");
            context.rateLimitReached();
            return Result.failed(HttpStatus.TOO_MANY_REQUESTS);
        }
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.beforeCommit(() -> {
            originalResponse.getHeaders()
                .set(X_RATE_LIMIT, String.valueOf(rateLimitResult.getNewTokens()));
            return Mono.empty();
        });
        return Result.success();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }

    private Mono<String> sanitizeRequestBody(ServerWebExchange exchange,
                                             CustomFilterContext context) {
        ServerRequest serverRequest =
            ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        return serverRequest.bodyToMono(String.class).map(e -> {
            String sanitize = jsonSanitizeService.sanitizeRequest(e);
            context.sanitizedContentLength = sanitize.getBytes().length;
            return sanitize;
        });
    }

    public Mono<String> readFormDataFromRequest(ServerWebExchange exchange, String authHeader,
                                                String requestUri,
                                                CustomFilterContext customFilterContext) {

        ServerRequest serverRequest =
            ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        return serverRequest.bodyToMono(String.class).map(body -> {
            Map<String, String> parameters;
            try {
                MultiPartStringParser
                    multiPartStringParser = new MultiPartStringParser(body);
                parameters = multiPartStringParser.getParameters();
            } catch (Exception e) {
                log.error("error during parse form data", e);
                customFilterContext.formParseError();
                return body;
            }
            try {
                if (!DomainRegistry.getRevokeTokenService()
                    .checkAccess(authHeader, requestUri, parameters)) {
                    customFilterContext.tokenRevoked();
                }
            } catch (ParseException e) {
                log.error("error during parse", e);
                customFilterContext.tokenCheckError();
                return body;
            }
            return body;
        });
    }

    public static class CachedBodyOutputMessage implements ReactiveHttpOutputMessage {
        private final DataBufferFactory bufferFactory;
        private final HttpHeaders httpHeaders;
        private Flux<DataBuffer> body = Flux.error(
            new IllegalStateException("The body is not set. Did handling complete with success?"));

        CachedBodyOutputMessage(ServerWebExchange exchange, HttpHeaders httpHeaders) {
            this.bufferFactory = exchange.getResponse().bufferFactory();
            this.httpHeaders = httpHeaders;
        }

        public void beforeCommit(Supplier<? extends Mono<Void>> action) {
        }

        public boolean isCommitted() {
            return false;
        }

        public HttpHeaders getHeaders() {
            return this.httpHeaders;
        }

        public DataBufferFactory bufferFactory() {
            return this.bufferFactory;
        }

        public Flux<DataBuffer> getBody() {
            return this.body;
        }

        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            this.body = Flux.from(body);
            return Mono.empty();
        }

        public Mono<Void> writeAndFlushWith(
            Publisher<? extends Publisher<? extends DataBuffer>> body) {
            return this.writeWith(Flux.from(body).flatMap((p) -> {
                return p;
            }));
        }

        public Mono<Void> setComplete() {
            return this.writeWith(Flux.empty());
        }
    }

    @Data
    public static class CustomFilterContext {
        private boolean endpointCheckFailed = false;
        private boolean rateLimitCheckFailed = false;
        private HttpStatus httpErrorStatus;
        private int sanitizedContentLength;
        private boolean requestCopiedSanitize = false;
        private boolean requestCopiedRevokeToken = false;
        private boolean tokenCheckFailed;
        private boolean etagRequired;
        private boolean isWebsocket;

        public void endpointCheckError() {
            this.endpointCheckFailed = true;
            this.httpErrorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        public void endpointCheckFailed() {
            this.endpointCheckFailed = true;
            this.httpErrorStatus = HttpStatus.FORBIDDEN;
        }

        public void endpointCheckSuccess() {
            this.endpointCheckFailed = false;
        }

        public boolean hasCheckFailed() {
            return tokenCheckFailed || endpointCheckFailed || rateLimitCheckFailed;
        }

        public void rateLimitReached() {
            this.rateLimitCheckFailed = true;
            this.httpErrorStatus = HttpStatus.TOO_MANY_REQUESTS;
        }

        public void tokenRevoked() {
            this.tokenCheckFailed = true;
            this.httpErrorStatus = HttpStatus.UNAUTHORIZED;
        }

        public void tokenCheckError() {
            this.tokenCheckFailed = true;
            this.httpErrorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        public void formParseError() {
            this.tokenCheckFailed = true;
            this.httpErrorStatus = HttpStatus.BAD_REQUEST;
        }

        public void requestCopiedForRevokeToken() {
            this.requestCopiedRevokeToken = true;
        }

        public void requestCopiedForSanitize() {
            this.requestCopiedSanitize = true;
        }

        public void etagGenerated() {
            this.etagRequired = true;
        }

        public boolean responseSanitizeRequired() {
            return !this.isWebsocket;
        }

        public boolean responseErrorCheckRequire() {
            return !this.isWebsocket;
        }
    }

    public static class MultiPartStringParser
        implements org.apache.commons.fileupload.UploadContext {

        private String postBody;
        private String boundary;
        private Map<String, String> parameters = new HashMap<>();

        public MultiPartStringParser(String postBody) throws Exception {
            this.postBody = postBody;
            // Sniff out the multpart boundary.
            this.boundary = postBody.substring(2, postBody.indexOf('\n')).trim();
            // Parse out the parameters.
            final FileItemFactory factory = new DiskFileItemFactory();
            FileUpload upload = new FileUpload(factory);
            List<FileItem> fileItems = upload.parseRequest(this);
            for (FileItem fileItem : fileItems) {
                if (fileItem.isFormField()) {
                    parameters.put(fileItem.getFieldName(), fileItem.getString());
                } // else it is an uploaded file
            }
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        // The methods below here are to implement the UploadContext interface.
        @Override
        public String getCharacterEncoding() {
            return "UTF-8"; // You should know the actual encoding.
        }

        // This is the deprecated method from RequestContext that unnecessarily
        // limits the length of the content to ~2GB by returning an int.
        @Override
        public int getContentLength() {
            return -1; // Don't use this
        }

        @Override
        public String getContentType() {
            // Use the boundary that was sniffed out above.
            return "multipart/form-data, boundary=" + this.boundary;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(postBody.getBytes());
        }

        @Override
        public long contentLength() {
            return postBody.length();
        }
    }

    @Data
    public static class Result<T> {
        private boolean result;
        private HttpStatus errorHttpStatus;
        private Mono<T> next;

        private Result(boolean b, Mono<T> next, HttpStatus errorHttpStatus) {
            result = b;
            this.next = next;
            this.errorHttpStatus = errorHttpStatus;
        }

        public static Result<Void> success() {
            return new Result<>(true, null, null);
        }

        public static <T> Result<T> success(Mono<T> next) {
            return new Result<>(true, next, null);
        }

        public static Result<Void> failed(HttpStatus errorHttpStatus) {
            return new Result<>(false, null, errorHttpStatus);
        }

        public static <T> Result<T> failed(Mono<T> next, HttpStatus errorHttpStatus) {
            return new Result<>(false, next, errorHttpStatus);
        }
    }
}