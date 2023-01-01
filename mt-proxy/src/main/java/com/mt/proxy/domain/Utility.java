package com.mt.proxy.domain;

import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;

public class Utility {
    public static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static boolean isWebSocket(HttpHeaders headers) {
        return "websocket".equals(headers.getUpgrade());
    }

    public static String getUuid(ServerHttpRequest request) {
        return request.getHeaders().getFirst(REQ_UUID);
    }
}
