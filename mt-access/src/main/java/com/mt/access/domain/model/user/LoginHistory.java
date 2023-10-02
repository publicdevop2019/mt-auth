package com.mt.access.domain.model.user;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class LoginHistory {
    @Setter(AccessLevel.PROTECTED)
    @Getter
    protected Long id;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private UserId userId;
    @Getter

    @Setter(AccessLevel.PRIVATE)
    private Long loginAt;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String ipAddress;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String agent;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private ProjectId projectId;

    public LoginHistory(UserLoginRequest command, ProjectId projectId) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.userId = command.getUserId();
        loginAt = Instant.now().toEpochMilli();
        ipAddress = command.getIpAddress();
        agent = command.getAgent();
        this.projectId = projectId;
    }

    private LoginHistory() {
    }

    public static LoginHistory create(Long id, UserId userId, Long loginAt, String ipAddress,
                                      String agent, ProjectId projectId) {
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setId(id);
        loginHistory.setUserId(userId);
        loginHistory.setLoginAt(loginAt);
        loginHistory.setIpAddress(ipAddress);
        loginHistory.setAgent(agent);
        loginHistory.setProjectId(projectId);
        return loginHistory;
    }

}
