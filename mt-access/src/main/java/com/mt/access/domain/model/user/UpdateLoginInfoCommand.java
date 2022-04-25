package com.mt.access.domain.model.user;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Getter
public class UpdateLoginInfoCommand {
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

    private static final String[] HEADERS_TO_TRY = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR" };

    private String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
