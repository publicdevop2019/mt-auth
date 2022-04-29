package com.mt.access.domain.model.user;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Getter
@Slf4j
public class UpdateLoginInfoCommand {
    private static final String[] HEADERS_TO_TRY = {
        "X-Real-IP",
        "X-Forwarded-For",
    };
    private final String ipAddress;
    private final String agent;
    private final UserId userId;

    public UpdateLoginInfoCommand(HttpServletRequest servletRequest,
                                  @NotNull OAuth2AccessToken body,
                                  String agentInfo) {
        this.ipAddress = getClientIpAddress(servletRequest);
        this.agent = agentInfo;
        if (body != null) {
            String o = (String) body.getAdditionalInformation().get(
                "uid");
            userId = new UserId(o);
        } else {
            userId = null;
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        if (log.isTraceEnabled()) {
            log.debug("--start of get client ip address");
            request.getHeaderNames().asIterator().forEachRemaining(e -> {
                log.debug("header name [{}] and value: {}", e, request.getHeader(e));
            });
        }
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        if (log.isTraceEnabled()) {
            log.debug("--end of get client ip address");
        }
        return request.getRemoteAddr();
    }
}
