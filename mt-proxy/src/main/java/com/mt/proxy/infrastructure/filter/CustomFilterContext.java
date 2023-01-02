package com.mt.proxy.infrastructure.filter;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomFilterContext {
    private boolean endpointCheckFailed = false;
    private boolean rateLimitCheckFailed = false;
    private HttpStatus httpErrorStatus;
    private int newContentLength;
    private boolean requestCopiedSanitize = false;
    private boolean requestCopiedRevokeToken = false;
    private boolean tokenCheckFailed;
    private boolean websocket;
    private String authHeader;
    private boolean bodyCopied = false;

    public void endpointCheckFailed() {
        this.endpointCheckFailed = true;
        this.httpErrorStatus = HttpStatus.FORBIDDEN;
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

    public void bodyReadRequired() {
        this.bodyCopied = true;

    }
}
