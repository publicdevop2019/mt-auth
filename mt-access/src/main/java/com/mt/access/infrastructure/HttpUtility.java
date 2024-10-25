package com.mt.access.infrastructure;

import static com.mt.access.infrastructure.AppConstant.QUERY_PROJECT_IDS;

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

    /**
     * add project id as part of query string
     * @param original query string to be appended
     * @param projectId project id
     * @return combined query string
     */
    public static String updateProjectIds(String original, String projectId) {
        if (original == null) {
            return QUERY_PROJECT_IDS + ":" + projectId;
        }
        original = original + "," + QUERY_PROJECT_IDS + ":" + projectId;
        return original;
    }
}
