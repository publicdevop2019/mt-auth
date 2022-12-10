package com.mt.proxy.domain;

import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;

public class Utility {
    public static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    public static boolean isWebSocket(HttpHeaders headers) {
        return "websocket".equals(headers.getUpgrade());
    }
}
