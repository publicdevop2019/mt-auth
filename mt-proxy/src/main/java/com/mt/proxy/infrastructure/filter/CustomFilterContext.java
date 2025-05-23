package com.mt.proxy.infrastructure.filter;

import com.mt.proxy.domain.Utility;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

@Data
public class CustomFilterContext {
    private Boolean endpointCheckFailed = false;
    private Boolean rateLimitCheckFailed = false;
    private HttpStatus httpErrorStatus;
    private Integer newContentLength;
    private Boolean tokenCheckFailed;
    private Boolean websocket;
    private String authHeader;
    private Boolean bodyCopied = false;

    public CustomFilterContext(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        setWebsocket(Utility.isWebSocket(request.getHeaders()));
        setAuthHeader(Utility.getAuthHeader(request));
    }

    public void endpointCheckFailed(HttpStatus status) {
        this.endpointCheckFailed = true;
        this.httpErrorStatus = status;
    }

    public boolean hasCheckFailed() {
        return Boolean.TRUE.equals(tokenCheckFailed) || Boolean.TRUE.equals(endpointCheckFailed) ||
            Boolean.TRUE.equals(rateLimitCheckFailed);
    }

    public void rateLimitReached() {
        this.rateLimitCheckFailed = true;
        this.httpErrorStatus = HttpStatus.TOO_MANY_REQUESTS;
    }

    public void tokenRevoked() {
        this.tokenCheckFailed = true;
        this.httpErrorStatus = HttpStatus.UNAUTHORIZED;
    }

    public void invalidRefreshToken() {
        this.tokenCheckFailed = true;
        this.httpErrorStatus = HttpStatus.UNAUTHORIZED;
    }

    public void bodyReadRequired() {
        this.bodyCopied = true;

    }
}
