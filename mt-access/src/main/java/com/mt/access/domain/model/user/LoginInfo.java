package com.mt.access.domain.model.user;

import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode
public class LoginInfo {
    @Setter(AccessLevel.PROTECTED)
    @Getter
    protected Long id;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private UserId userId;
    @Getter
    private Long loginAt;
    @Getter
    private String ipAddress;
    @Getter
    private String agent;

    public LoginInfo(UserLoginRequest command) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.userId = command.getUserId();
        loginAt = Instant.now().toEpochMilli();
        ipAddress = command.getIpAddress();
        agent = command.getAgent();
    }

    public static LoginInfo create(Long id, UserId userId, Long loginAt, String ipAddress,
                                   String agent) {
        LoginInfo info = new LoginInfo();
        info.setId(id);
        info.setUserId(userId);
        info.loginAt = loginAt;
        info.ipAddress = ipAddress;
        info.agent = agent;
        return info;
    }
}
