package com.mt.access.domain.model.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class UserLoginRequest {

    private final String ipAddress;
    private final String agent;
    private final UserId userId;

    public UserLoginRequest(String ipAddress,
                            UserId userId,
                            String agentInfo) {
        this.ipAddress = ipAddress;
        this.agent = agentInfo;
        this.userId = userId;
    }

}
