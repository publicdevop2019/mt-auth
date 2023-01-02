package com.mt.proxy.infrastructure.filter;

import com.google.json.JsonSanitizer;
import com.mt.proxy.domain.CacheConfiguration;
import com.mt.proxy.domain.CacheService;
import com.mt.proxy.domain.DomainRegistry;
import com.mt.proxy.domain.JsonSanitizeService;
import com.mt.proxy.domain.Utility;
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

    private static ServerHttpRequestDecorator decorateRequest(ServerWebExchange exchange,
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

    private static byte[] sanitizeResp(byte[] responseBody, ServerHttpResponse originalResponse) {
        HttpHeaders headers = originalResponse.getHeaders();
        if (MediaType.APPLICATION_JSON_UTF8.equals(headers.getContentType())) {
            String responseBodyString =
                new String(responseBody, StandardCharsets.UTF_8);
            String afterSanitize = JsonSanitizer.sanitize(responseBodyString);
            byte[] bytes = afterSanitize.getBytes(StandardCharsets.UTF_8);
            if (headers.getContentLength()
                !=
                afterSanitize.getBytes(StandardCharsets.UTF_8).length) {
                log.debug("sanitized response length before {} after {}",
                    responseBody.length, bytes.length);
                headers.setContentLength(bytes.length);
            }
            return bytes;
        }
        return responseBody;
    }

    private static boolean responseError(ServerHttpResponse originalResponse) {
        log.debug("checking response in case of downstream error");
        boolean b = originalResponse.getStatusCode() != null
            &&
            originalResponse.getStatusCode().is5xxServerError();
        if (b) {
            originalResponse.getHeaders().setContentLength(0);
        }
        return b;
    }

    private static byte[] updateGzip(byte[] responseBody, ServerHttpResponse originalResponse) {
        if (originalResponse.getHeaders().getContentType() != null
            && originalResponse.getHeaders().getContentType()
            .equals(MediaType.APPLICATION_JSON_UTF8)
        ) {
            boolean minLength = responseBody.length > 1024;
            if (minLength) {
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
                return compressed;
            }
        }
        return responseBody;
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
        context.isWebsocket = Utility.isWebSocket(request.getHeaders());
        context.authHeader = Utility.getAuthHeader(request);
        ServerHttpResponse response = exchange.getResponse();
        checkEndpoint(exchange, context);
        if (context.hasCheckFailed()) {
            response.setStatusCode(context.httpErrorStatus);
            return response.setComplete();
        }
        if (!context.isWebsocket) {
            checkRateLimit(exchange, context);
            if (context.hasCheckFailed()) {
                response.setStatusCode(context.httpErrorStatus);
                return response.setComplete();
            }
        }
        Mono<ServerHttpRequest> requestMono = checkReqBeforeSend(exchange, context);

        if (context.hasCheckFailed()) {
            response.setStatusCode(context.httpErrorStatus);
            return response.setComplete();
        }
        if (context.isWebsocket) {
            //for websocket only endpoint & token check is performed
            //@todo fix token check for websocket
            return requestMono.flatMap(req -> {
                if (context.hasCheckFailed()) {
                    response.setStatusCode(context.httpErrorStatus);
                    return response.setComplete();
                }
                return chain.filter(exchange);
            });
        }

        ServerHttpResponse updatedResp = updateResponse(exchange);

        return requestMono.flatMap(req -> {
            if (context.hasCheckFailed()) {
                response.setStatusCode(context.httpErrorStatus);
                return response.setComplete();
            }
            if (context.bodyCopied) {
                return chain.filter(exchange.mutate().request(req).response(updatedResp).build());
            } else {
                return chain.filter(exchange.mutate().response(updatedResp).build());
            }
        });
    }

    private ServerHttpResponse updateResponse(ServerWebExchange exchange) {
        addCsrfHeader(exchange);
        addCacheHeader(exchange);
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                boolean b = responseError(originalResponse);
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
                        byte[] sanitizedBody = sanitizeResp(responseBody, originalResponse);
                        byte[] zippedBody = updateGzip(sanitizedBody, originalResponse);
                        updateEtag(zippedBody, exchange, originalResponse);
                        return bufferFactory.wrap(zippedBody);
                    }));
                }
                return super.writeWith(body);
            }

        };
    }

    private void updateEtag(byte[] responseBody, ServerWebExchange exchange,
                            ServerHttpResponse originalResponse) {
        CacheConfiguration cacheConfiguration =
            cacheService.getCacheConfiguration(exchange, true);
        if (cacheConfiguration != null && cacheConfiguration.isEtag() &&
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
    private ServerHttpResponse addCsrfHeader(ServerWebExchange exchange) {
        if (exchange.getRequest().getCookies().get("XSRF-TOKEN") == null
            &&
            exchange.getRequest().getHeaders().get("x-xsrf-token") == null) {
            String var0 = UUID.randomUUID().toString();
            exchange.getResponse().getHeaders().add(HttpHeaders.SET_COOKIE,
                "XSRF-TOKEN=" + var0 + "; SameSite=None; Path=/; Secure; Domain=" + domain);
        }
        return exchange.getResponse();
    }

    private Mono<ServerHttpRequest> checkReqBeforeSend(ServerWebExchange exchange,
                                                       CustomFilterContext context) {
        ServerHttpRequest request = exchange.getRequest();
        //due to netty performance issue
        if (Utility.isTokenRequest(request)
            ||
            jsonSanitizeService
                .sanitizeRequired(request.getMethod(), request.getHeaders().getContentType())) {
            context.bodyReadRequired();
            ServerRequest serverRequest =
                ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).map(body -> {
                if (Utility.isTokenRequest(request)) {
                    Map<String, String> parameters;
                    try {
                        MultiPartStringParser
                            multiPartStringParser = new MultiPartStringParser(body);
                        parameters = multiPartStringParser.getParameters();
                    } catch (Exception e) {
                        log.error("error during parse form data", e);
                        context.formParseError();
                        return body;
                    }
                    try {
                        if (!DomainRegistry.getRevokeTokenService()
                            .checkAccess(context.authHeader, request.getPath().toString(),
                                parameters)) {
                            context.tokenRevoked();
                        }
                    } catch (ParseException e) {
                        log.error("error during parse", e);
                        context.tokenCheckError();
                        return body;
                    }
                } else {
                    String sanitize = jsonSanitizeService.sanitizeRequest(body);
                    context.sanitizedContentLength = sanitize.getBytes().length;
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
                if (jsonSanitizeService
                    .sanitizeRequired(request.getMethod(), request.getHeaders().getContentType())) {
                    headers.setContentLength(context.sanitizedContentLength);
                }
                return Mono.just(decorateRequest(exchange, headers, outputMessage));
            }));
        } else {
            try {
                if (!DomainRegistry.getRevokeTokenService()
                    .checkAccess(context.authHeader, request.getPath().toString(), null)) {
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
        log.debug("start of check endpoint");
        ServerHttpRequest request = exchange.getRequest();
        log.trace("endpoint path: {} scheme: {}", exchange.getRequest().getURI().getPath(),
            exchange.getRequest().getURI().getScheme());
        boolean allow;
        try {
            allow = DomainRegistry.getEndpointService().checkAccess(
                request.getPath().toString(),
                request.getMethod().name(),
                context.authHeader, context.isWebsocket);
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
        log.debug("end of check endpoint");
        context.endpointCheckSuccess();
    }

    private void checkRateLimit(ServerWebExchange exchange, CustomFilterContext context) {
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
        }
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.beforeCommit(() -> {
            originalResponse.getHeaders()
                .set(X_RATE_LIMIT, String.valueOf(rateLimitResult.getNewTokens()));
            return Mono.empty();
        });
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
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
        private boolean isWebsocket;
        private String authHeader;
        private boolean bodyCopied = false;

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

        public void bodyReadRequired() {
            this.bodyCopied = true;

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
}