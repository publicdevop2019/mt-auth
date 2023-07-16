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
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "loginHistoryRegion")
@EqualsAndHashCode
public class LoginHistory {
    @Id
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    protected Long id;
    @Embedded
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private UserId userId;
    @Getter
    private Long loginAt;
    @Getter
    private String ipAddress;
    @Getter
    private String agent;

    public LoginHistory(UserLoginRequest command) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.userId = command.getUserId();
        loginAt = Instant.now().toEpochMilli();
        ipAddress = command.getIpAddress();
        agent = command.getAgent();
    }

}
