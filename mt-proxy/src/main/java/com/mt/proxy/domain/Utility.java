package com.mt.proxy.domain;

import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;

import org.bouncycastle.util.encoders.Base64;
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

    /**
     * get auth header for both regular http and websocket
     * @param request ServerHttpRequest
     * @return authorization header
     */
    public static String getAuthHeader(ServerHttpRequest request){
        if (isWebSocket(request.getHeaders())) {
            String temp = request.getQueryParams().getFirst("jwt");
            if (temp != null) {
                return "Bearer " + new String(Base64.decode(temp));
            }
            return null;
        } else {
            return request.getHeaders().getFirst("authorization");
        }
    }
    public static boolean isTokenRequest(ServerHttpRequest request){
        return request.getPath().toString().contains("/oauth/token");
    }
}
