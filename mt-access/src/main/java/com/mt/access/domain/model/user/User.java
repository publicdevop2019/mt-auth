package com.mt.access.domain.model.user;

import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
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
    private MfaInfo mfaInfo;

    private User(UserId userId, UserMobile mobile) {
        super();
        setMobile(mobile);
        initNewUserParams(userId);
    }

    private User(UserId userId, UserMobile mobile, UserPassword password) {
        super();
        setMobile(mobile);
        setPassword(password);
        initNewUserParams(userId);
    }

    private User(UserId userId, UserEmail email) {
        super();
        setEmail(email);
        initNewUserParams(userId);
    }

    private User(UserId userId, UserEmail email, UserPassword password) {
        super();
        setEmail(email);
        setPassword(password);
        initNewUserParams(userId);
    }

    private User(UserId userId, UserName username, UserPassword password) {
        super();
        setUserName(username);
        setPassword(password);
        initNewUserParams(userId);
    }

    private User() {
    }

    public static User fromDatabaseRow(Long id, Long createdAt, String createdBy, Long modifiedAt,
                                       String modifiedBy, Integer version,
                                       UserEmail email, Boolean locked, UserPassword userPassword,
                                       UserId domainId,
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
        user.setUserId(domainId);
        user.setUserName(userName);
        user.setMobile(userMobile);
        user.setUserAvatar(userAvatar);
        user.setLanguage(language);
        user.mfaInfo = mfaInfo;
        return user;
    }

    public static User newUser(UserMobile mobile, UserId userId) {
        return new User(userId, mobile);
    }

    public static User newUser(UserMobile mobile, UserPassword password, UserId userId) {
        return new User(userId, mobile, password);
    }

    public static User newUser(UserEmail email, UserId userId) {
        return new User(userId, email);
    }

    public static User newUser(UserEmail email, UserPassword password, UserId userId) {
        return new User(userId, email, password);
    }

    public static User newUser(UserName username, UserPassword password, UserId userId) {
        return new User(userId, username, password);
    }

    private void setLocked(Boolean locked) {
        Validator.notNull(locked);
        this.locked = locked;
    }

    public String getDisplayName() {
        if (Checker.notNull(userName)) {
            return userName.getValue();
        }
        if (Checker.notNull(email)) {
            return email.getEmail();
        }
        if (Checker.notNull(mobile)) {
            return mobile.getValue();
        }
        return null;
    }

    @Override
    public void validate(ValidationNotificationHandler handler) {
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
            Objects.equals(mfaInfo, update.mfaInfo);
    }

    public User updatePassword(UserPassword password) {
        User user = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        user.setPassword(password);
        return user;
    }

    private void initNewUserParams(UserId userId) {
        setUserId(userId);
        setLocked(Boolean.FALSE);
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        long milli = Instant.now().toEpochMilli();
        setCreatedAt(milli);
        setCreatedBy(userId.getDomainId());
        setModifiedAt(milli);
        setModifiedBy(userId.getDomainId());
    }

    public User addUserName(UserName userName) {
        User update = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, User.class);
        update.userName = userName;
        return update;
    }

    public void checkUserNameRemoval() {
        if (email == null && mobile == null) {
            throw new DefinedRuntimeException("email, mobile, username need to keep at least one",
                "1063",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public void checkMobileRemoval() {
        if (email == null && userName == null) {
            throw new DefinedRuntimeException("email, mobile, username need to keep at least one",
                "1063",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public void checkEmailRemoval() {
        if (mobile == null && userName == null) {
            throw new DefinedRuntimeException("email, mobile, username need to keep at least one",
                "1063",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public User removeUserName() {
        User update = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, User.class);
        update.userName = null;
        return update;
    }

    public User addMobile(UserMobile newMobile) {
        User update = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, User.class);
        update.mobile = newMobile;
        return update;
    }

    public User removeMobile() {
        User update = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, User.class);
        update.mobile = null;
        return update;
    }

    public User removeEmail() {
        User update = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, User.class);
        update.email = null;
        return update;
    }

    public User addEmail(UserEmail userEmail) {
        User update = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, User.class);
        update.email = userEmail;
        return update;
    }

    public User updateLanguage(Language language) {
        User update = CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, User.class);
        update.language = language;
        return update;
    }

    public boolean hasMultipleMfaOptions() {
        return Checker.notNull(mobile) && Checker.notNull(email);
    }

    public boolean hasNoMfaOptions() {
        return Checker.isNull(mobile) && Checker.isNull(email);
    }
}
