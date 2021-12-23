package com.mt.proxy.infrastructure.springcloudgateway;

import com.mt.proxy.domain.DomainRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.reactivestreams.Publisher;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
public class SCGRevokeTokenFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = null;
        ServerHttpRequest request = exchange.getRequest();
        List<String> authorization = request.getHeaders().get("authorization");
        if (authorization != null && !authorization.isEmpty()) {
            authHeader = authorization.get(0);
        }
        //due to netty performance issue
        if (request.getPath().toString().contains("/oauth/token")) {
            log.debug("checking revoke token");
            GatewayContext gatewayContext = new GatewayContext();
            Mono<String> modifiedBody = readFormDataFromRequest(exchange, authHeader, request.getPath().toString(), gatewayContext);
            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                ServerHttpRequest decorator = decorate(exchange, headers, outputMessage);
                if (gatewayContext.shouldBlock) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                } else {
                    return chain.filter(exchange.mutate().request(decorator).build());
                }
            }));
        } else {
            ServerHttpResponse response = exchange.getResponse();
            try {
                if (!DomainRegistry.getRevokeTokenService().checkAccess(authHeader, request.getPath().toString(), null)) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }
            } catch (ParseException e) {
                log.error("error during parse", e);
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                return response.setComplete();
            }
            return chain.filter(exchange);
        }
    }

    public static ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
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
    public int getOrder() {
        return 0;
    }

    private Mono<String> readFormDataFromRequest(ServerWebExchange exchange, String authHeader, String requestURI, GatewayContext gatewayContext) {
        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        return serverRequest.bodyToMono(String.class).map(body -> {
            try {
                MultiPartStringParser multiPartStringParser = new MultiPartStringParser(body);
                Map<String, String> parameters = multiPartStringParser.getParameters();
                if (!DomainRegistry.getRevokeTokenService().checkAccess(authHeader, requestURI, parameters)) {
                    gatewayContext.setShouldBlock(true);
                }
            } catch (Exception e) {
                log.error("error during parse form data", e);
                gatewayContext.setShouldBlock(true);
            }
            return body;
        });
    }

    public static class CachedBodyOutputMessage implements ReactiveHttpOutputMessage {
        private final DataBufferFactory bufferFactory;
        private final HttpHeaders httpHeaders;
        private Flux<DataBuffer> body = Flux.error(new IllegalStateException("The body is not set. Did handling complete with success?"));

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

        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            return this.writeWith(Flux.from(body).flatMap((p) -> {
                return p;
            }));
        }

        public Mono<Void> setComplete() {
            return this.writeWith(Flux.empty());
        }
    }

    @Data
    public static class GatewayContext {
        private boolean shouldBlock = false;
    }

    public static class MultiPartStringParser implements org.apache.commons.fileupload.UploadContext {

        private String postBody;
        private String boundary;
        private Map<String, String> parameters = new HashMap<String, String>();

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
