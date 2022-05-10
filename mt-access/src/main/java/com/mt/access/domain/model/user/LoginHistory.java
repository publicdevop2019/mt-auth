package com.mt.access.domain.model.user;

import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "login_history")
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "loginHistoryRegion")
public class LoginHistory {
    @Id
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    protected Long id;
    @Embedded
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private UserId userId;
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    private Date loginAt;
    @Getter
    private String ipAddress;
    @Getter
    private String agent;

    public LoginHistory(UserLoginRequest command) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.userId = command.getUserId();
        loginAt = Date.from(Instant.now());
        ipAddress = command.getIpAddress();
        agent = command.getAgent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoginHistory that = (LoginHistory) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) &&
            Objects.equals(loginAt, that.loginAt) &&
            Objects.equals(ipAddress, that.ipAddress) &&
            Objects.equals(agent, that.agent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, loginAt, ipAddress, agent);
    }
}
