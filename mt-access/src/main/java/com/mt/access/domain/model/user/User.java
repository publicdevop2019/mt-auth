package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * user aggregate.
 */
@EqualsAndHashCode(callSuper = true)
public class User extends Auditable {
    private static final String[] ROOT_ACCOUNTS = {"0U8AZTODP4H0"};

    @Setter(AccessLevel.PRIVATE)
    @Getter
    private UserEmail email;

    @Getter
    @Setter
    private UserPassword password;

    @Getter
    @Setter
    private UserMobile mobile;

    @Getter
    @Setter
    private UserAvatar userAvatar;

    @Getter
    @Setter
    private UserName userName;

    @Getter
    @Setter
    private Language language;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private UserId userId;

    @Getter
    private Boolean locked;

    @Getter
    private PasswordResetCode pwdResetToken;
    @Getter
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
        long milli = Instant.now().toEpochMilli();
        setCreatedAt(milli);
        setCreatedBy(userId.getDomainId());
        setModifiedAt(milli);
        setModifiedBy(userId.getDomainId());
    }

    public static User fromDatabaseRow(Long id, Long createdAt, String createdBy, Long modifiedAt,
                                       String modifiedBy, Integer version,
                                       UserEmail email, Boolean locked, UserPassword userPassword,
                                       PasswordResetCode passwordResetCode, UserId domainId,
                                       UserName userName,
                                       UserMobile userMobile, UserAvatar userAvatar,
                                       Language language, MfaInfo mfaInfo) {
        User user = new User();
        user.setId(id);
        user.setCreatedAt(createdAt);
        user.setCreatedBy(createdBy);
        user.setModifiedAt(modifiedAt);
        user.setModifiedBy(modifiedBy);
        user.setVersion(version);
        user.setEmail(email);
        user.setLocked(locked);
        user.setPassword(userPassword);
        user.pwdResetToken = passwordResetCode;
        user.setUserId(domainId);
        user.setUserName(userName);
        user.setMobile(userMobile);
        user.setUserAvatar(userAvatar);
        user.setLanguage(language);
        user.mfaInfo = mfaInfo;
        return user;
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
    public void validate(ValidationNotificationHandler handler) {
    }

    public void setPwdResetToken(PasswordResetCode pwdResetToken, TransactionContext context) {
        this.pwdResetToken = pwdResetToken;
        context
            .append(new UserPwdResetCodeUpdated(getUserId(), getEmail(), getPwdResetToken()));
    }


    public User lockUser(Boolean locked, TransactionContext context) {
        User user = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, User.class);
        if (Arrays.stream(ROOT_ACCOUNTS)
            .anyMatch(e -> e.equalsIgnoreCase(user.userId.getDomainId()))) {
            throw new DefinedRuntimeException("root account cannot be locked", "1062",
                HttpResponseCode.BAD_REQUEST);
        }
        context.append(new UserGetLocked(userId));
        user.setLocked(locked);
        return user;
    }

    public User update(UserMobile mobile,
                       @Nullable UserName userName,
                       @Nullable Language language) {
        User update = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, User.class);
        if (userName != null) {
            if (update.userName != null && update.userName.getValue() != null
                && !update.userName.equals(userName)) {
                throw new DefinedRuntimeException("username can only be set once", "1063",
                    HttpResponseCode.BAD_REQUEST);
            }
            update.userName = userName;
        }
        if (language != null) {
            update.language = language;
        }
        if (!update.mobile.equals(mobile)) {
            update.mobile = mobile;
        }
        return update;
    }

    public User updateUserAvatar(UserAvatar userAvatar) {
        User user = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, User.class);
        user.setUserAvatar(userAvatar);
        return user;
    }

    public boolean sameAs(User update) {
        return Objects.equals(email, update.email) &&
            Objects.equals(password, update.password) &&
            Objects.equals(mobile, update.mobile) &&
            Objects.equals(userAvatar, update.userAvatar) &&
            Objects.equals(userName, update.userName) && language == update.language &&
            Objects.equals(userId, update.userId) &&
            Objects.equals(locked, update.locked) &&
            Objects.equals(pwdResetToken, update.pwdResetToken) &&
            Objects.equals(mfaInfo, update.mfaInfo);
    }
}
