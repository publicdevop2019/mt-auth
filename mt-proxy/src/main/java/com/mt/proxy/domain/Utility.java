package com.mt.proxy.domain;

import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;

import java.util.HashMap;
import java.util.Map;
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

    public static Map<String, String> readFormData(String body) throws IndexOutOfBoundsException {
        Map<String, String> parameters = new HashMap<>();
        String stripEndingDash = body.substring(0, body.length() - 4);
        String boundary = stripEndingDash.substring(0, stripEndingDash.indexOf('\n') + 1);
        String replace = stripEndingDash.replaceFirst(boundary, "");
        String[] split = replace.split(boundary);
        for (String s : split) {
            String[] fields = s.split("\r\n");
            String s1 = fields[0];
            String key =
                s1.replace("Content-Disposition: form-data; name=\"", "").replace("\"", "");
            String value = fields[fields.length-1];
            parameters.put(key, value);
        }
        return parameters;
    }

    public static String getClientIp(ServerHttpRequest request) {
        String clientIp = "NOT_FOUND";
        String first = request.getHeaders().getFirst("X-FORWARDED-FOR");
        if (first != null) {
            clientIp = first;
        } else {
            if (request.getRemoteAddress() != null) {
                clientIp = request.getRemoteAddress().toString();
            }
        }
        return clientIp;
    }
}
