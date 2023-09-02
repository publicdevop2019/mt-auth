package com.mt.access.domain.model.user;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "login_history")
@EqualsAndHashCode
public class LoginHistory {
    @Id
    @Setter(AccessLevel.PROTECTED)
    @Getter
    protected Long id;
    @Embedded
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
    @Embedded
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
        @AttributeOverride(name = "domainId",
            column = @Column(name = "projectId", updatable = false, nullable = false))
    })
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
