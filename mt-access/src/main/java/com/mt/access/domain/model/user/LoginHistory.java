package com.mt.access.domain.model.user;

import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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

    public LoginHistory(UserLoginRequest command) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.userId = command.getUserId();
        loginAt = Instant.now().toEpochMilli();
        ipAddress = command.getIpAddress();
        agent = command.getAgent();
    }

    private LoginHistory() {
    }

    public static LoginHistory create(Long id, UserId userId, Long loginAt, String ipAddress, String agent) {
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setId(id);
        loginHistory.setUserId(userId);
        loginHistory.setLoginAt(loginAt);
        loginHistory.setIpAddress(ipAddress);
        loginHistory.setAgent(agent);
        return loginHistory;
    }

}
