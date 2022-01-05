package com.mt.access.domain.model.user;

import com.google.common.base.Objects;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.access.domain.model.user.event.UserAuthorityChanged;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.access.domain.model.user.event.UserUpdated;
import com.mt.access.infrastructure.AppConstant;
import com.mt.access.port.adapter.persistence.system_role.SystemRoleIdConverter;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * root has ROLE_ROOT, ROLE_ADMIN, ROLE_USER
 * admin has ROLE_ADMIN, ROLE_USER
 * user has ROLE_USER
 */
@Entity
@Table(name = "user_")
@NoArgsConstructor
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "userRegion")
public class User extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Long id;
    @Setter(AccessLevel.PRIVATE)
    @Embedded
    @Getter
    private UserEmail email;
    @Embedded
    @Getter
    @Setter
    private UserPassword password;

    @Embedded
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private UserId userId;
    @Column
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private boolean locked = false;

    @Getter
    @Embedded
    private PasswordResetCode pwdResetToken;
    @Column
    @Getter
    @Convert(converter = SystemRoleIdConverter.class)
    private Set<SystemRoleId> roles;
    @Column
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private boolean subscription;

    public User(UserEmail userEmail, UserPassword password, UserId userId) {
        setEmail(userEmail);
        setPassword(password);
        setUserId(userId);
        setLocked(false);
        setRoles(Collections.singleton(new SystemRoleId(AppConstant.MT_AUTH_DEFAULT_USER_ROLE)));
        setSubscription(false);
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        DomainRegistry.getUserValidationService().validate(this, new HttpValidationNotificationHandler());
    }

    private void setRoles(Set<SystemRoleId> roles) {
        if (this.roles == null) {
            this.roles = Collections.emptySet();
        }
        if (id != null) {
            if (!getRoles().equals(roles)) {
                DomainEventPublisher.instance().publish(new UserAuthorityChanged(getUserId()));
            }
        }
        if (roles.contains(new SystemRoleId(AppConstant.MT_AUTH_ADMIN_ROLE))
                &&
                !this.roles.contains(new SystemRoleId(AppConstant.MT_AUTH_ADMIN_ROLE))) {
            throw new IllegalArgumentException("assign root role is prohibited");
        }
        if (!roles.contains(new SystemRoleId(AppConstant.MT_AUTH_ADMIN_ROLE))
                &&
                this.roles.contains(new SystemRoleId(AppConstant.MT_AUTH_ADMIN_ROLE))) {
            throw new IllegalArgumentException("remove root role is prohibited");
        }
        this.roles = roles;
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
        (new UserValidator(this, handler)).validate();
    }

    public void setPwdResetToken(PasswordResetCode pwdResetToken) {
        this.pwdResetToken = pwdResetToken;
        DomainEventPublisher.instance().publish(new UserPwdResetCodeUpdated(getUserId(), getEmail(), getPwdResetToken()));
    }

    public boolean isNonRoot() {
        return getRoles().stream().noneMatch(e -> AppConstant.MT_AUTH_ADMIN_ROLE.equals(e.getDomainId()));
    }

    public void replace(Set<SystemRoleId> roleIds, boolean locked, boolean subscription) {
        if (isSubscription() != subscription && !DomainRegistry.getAuthenticationService().userInRole(new SystemRoleId(AppConstant.MT_AUTH_ADMIN_ROLE)))
            throw new IllegalArgumentException("only root user can change subscription");
        if (!getRoles().equals(roleIds) && !DomainRegistry.getAuthenticationService().userInRole(new SystemRoleId(AppConstant.MT_AUTH_ADMIN_ROLE)))
            throw new IllegalArgumentException("only root user can change roles");
        if (Boolean.TRUE.equals(locked)) {
            DomainEventPublisher.instance().publish(new UserGetLocked(getUserId()));
        }
        setRoles(roleIds);
        setLocked(locked);
        setSubscription(subscription);
        validate(new HttpValidationNotificationHandler());
    }

    @PreUpdate
    private void preUpdate() {
        DomainEventPublisher.instance().publish(new UserUpdated(getUserId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equal(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), userId);
    }

    public void removeRole(SystemRoleId systemRoleId) {
        Set<SystemRoleId> collect = this.roles.stream().filter(e -> !e.equals(systemRoleId)).collect(Collectors.toSet());
        setRoles(collect);
    }
}
