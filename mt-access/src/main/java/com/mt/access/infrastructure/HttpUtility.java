package com.mt.access.infrastructure;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtility {
    private static final String[] HEADERS_TO_TRY = {
        "X-Real-IP",
        "X-Forwarded-For",
    };

    public static String getClientIpAddress(HttpServletRequest request) {
        log.trace("--start of get client ip address");
        request.getHeaderNames().asIterator().forEachRemaining(e -> {
            log.trace("header name [{}] and value: {}", e, request.getHeader(e));
        });
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        log.trace("--end of get client ip address");
        return request.getRemoteAddr();
    }
}
