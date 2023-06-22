package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.Arrays;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * user aggregate.
 */
@Embeddable
@Entity
@Table(name = "user_")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "userRegion")
@EqualsAndHashCode(callSuper = true)
public class User extends Auditable {
    private static final String[] ROOT_ACCOUNTS = {"0U8AZTODP4H0"};
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
    @Setter
    private UserMobile mobile;
    @Embedded
    @Getter
    @Setter
    @Nullable
    private UserAvatar userAvatar;
    @Embedded
    @Getter
    @Setter
    private UserName userName;
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Language language;
    @Embedded
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private UserId userId;
    @Column
    @Getter
    private Boolean locked;

    @Getter
    @Embedded
    private PasswordResetCode pwdResetToken;
    @Getter
    @Setter
    @Embedded
    private MfaInfo mfaInfo;

    private User(UserEmail userEmail, UserPassword password, UserId userId, UserMobile mobile) {
        super();
        setEmail(userEmail);
        setPassword(password);
        setUserId(userId);
        setLocked(Boolean.FALSE);
        setMobile(mobile);
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        DomainRegistry.getUserValidationService()
            .validate(this, new HttpValidationNotificationHandler());
    }

    private void setLocked(Boolean locked) {
        Validator.notNull(locked);
        this.locked = locked;
    }

    private User() {
    }

    public static User newUser(UserEmail userEmail, UserPassword password, UserId userId,
                               UserMobile mobile) {
        return new User(userEmail, password, userId, mobile);
    }

    public String getDisplayName() {
        if (userName != null) {
            return userName.getValue();
        }
        return email.getEmail();
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
    }

    public void setPwdResetToken(PasswordResetCode pwdResetToken) {
        this.pwdResetToken = pwdResetToken;
        CommonDomainRegistry.getDomainEventRepository()
            .append(new UserPwdResetCodeUpdated(getUserId(), getEmail(), getPwdResetToken()));
    }


    public void lockUser(Boolean locked) {
        if (Arrays.stream(ROOT_ACCOUNTS)
            .anyMatch(e -> e.equalsIgnoreCase(this.userId.getDomainId()))) {
            throw new DefinedRuntimeException("root account cannot be locked", "1062",
                HttpResponseCode.BAD_REQUEST);
        }
        CommonDomainRegistry.getDomainEventRepository().append(new UserGetLocked(userId));
        setLocked(locked);
    }

    public void update(UserMobile mobile,
                       @Nullable UserName userName,
                       @Nullable Language language) {
        if (userName != null) {
            if (this.userName != null && this.userName.getValue() != null
                && !this.userName.equals(userName)) {
                throw new DefinedRuntimeException("username can only be set once", "1063",
                    HttpResponseCode.BAD_REQUEST);
            }
            this.userName = userName;
        }
        if (language != null) {
            this.language = language;
        }
        if (!this.mobile.equals(mobile)) {
            this.mobile = mobile;
        }
    }
}
