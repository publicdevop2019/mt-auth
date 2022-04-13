package com.mt.access.domain.model.user;

import javax.servlet.ServletRequest;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Getter
public class UpdateLoginInfoCommand {
    private final String ipAddress;
    private final String agent;
    private final UserId userId;

    public UpdateLoginInfoCommand(ServletRequest servletRequest, @NotNull OAuth2AccessToken body,
                                  String agentInfo) {
        this.ipAddress = servletRequest.getRemoteAddr();
        this.agent = agentInfo;
        if (body != null) {
            String o = (String) body.getAdditionalInformation().get(
                "uid");
            userId = new UserId(o);
        } else {
            userId = null;
        }
    }
}
