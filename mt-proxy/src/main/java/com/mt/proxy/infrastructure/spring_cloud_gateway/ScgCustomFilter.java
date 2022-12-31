package com.mt.proxy.infrastructure.spring_cloud_gateway;

import static com.mt.proxy.domain.Utility.isWebSocket;

import com.mt.proxy.domain.DomainRegistry;
import com.mt.proxy.domain.JsonSanitizeService;
import com.mt.proxy.domain.rate_limit.RateLimitResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.bouncycastle.util.encoders.Base64;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        CustomFilterContext context = new CustomFilterContext();
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
        Mono<ServerHttpRequest> sanitizedRequestMono = sanitizeRequest(exchange, context);
        return requestMono.flatMap(req -> {
            if (context.hasCheckFailed()) {
                response.setStatusCode(context.httpErrorStatus);
                return response.setComplete();
            }
            return sanitizedRequestMono.flatMap(sanitizedReq -> {
                if(context.requestCopiedRevokeToken && context.requestCopiedSanitize){
                    return chain.filter(exchange.mutate().request(req).request(sanitizedReq).build());
                }else if(context.requestCopiedRevokeToken){
                    return chain.filter(exchange.mutate().request(req).build());
                }else if(context.requestCopiedSanitize){
                    return chain.filter(exchange.mutate().request(sanitizedReq).build());
                }
                return chain.filter(exchange);
            });
        });
//        return requestMono.flatMap(req -> {
//            if (context.hasCheckFailed()) {
//                response.setStatusCode(context.httpErrorStatus);
//                return response.setComplete();
//            }
//            Mono<Void> filter = chain.filter(exchange.mutate().request(req).build());
//            return sanitizedRequest.flatMap(sanitizedReq -> {
//                Mono<Void> filter1 = chain.filter(exchange.mutate().request(sanitizedReq).build());
//                return filter.then(filter1);
//            });
//        });

//        return  sanitizedRequestMono.flatMap(e-> {
//            if (context.hasCheckFailed()) {
//                response.setStatusCode(context.httpErrorStatus);
//                return response.setComplete();
//            }
//            return chain.filter(exchange.mutate().request(e).build());
//        });
//        return chain.filter(exchange);

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

    private ServerHttpRequest requestModifier() {
        return null;
    }

    private ServerHttpResponse responseModifier1() {
        return null;
    }

    private void responseModifier2() {

    }

    private void responseModifier3() {

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
        return HIGHEST_PRECEDENCE;
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
            try {
                MultiPartStringParser
                    multiPartStringParser = new MultiPartStringParser(body);
                Map<String, String> parameters = multiPartStringParser.getParameters();
                if (!DomainRegistry.getRevokeTokenService()
                    .checkAccess(authHeader, requestUri, parameters)) {
                    customFilterContext.tokenRevoked();
                }
            } catch (Exception e) {
                log.error("error during parse form data", e);
                customFilterContext.tokenCheckError();
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
        private boolean requestCopiedSanitize =false;
        private boolean requestCopiedRevokeToken =false;
        private boolean tokenCheckFailed;

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

        public void requestCopiedForRevokeToken() {
            this.requestCopiedRevokeToken=true;
        }

        public void requestCopiedForSanitize() {
            this.requestCopiedSanitize =true;
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