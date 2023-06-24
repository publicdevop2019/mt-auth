package com.mt.proxy.infrastructure.filter;

import lombok.Data;
import org.springframework.http.HttpStatus;

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
