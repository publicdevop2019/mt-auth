package com.mt.access.domain.model.user;

import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    default User get(UserId userId) {
        User user = query(userId).orElse(null);
        Validator.notNull(user);
        return user;
    }

    MfaInfo getUserMfaInfo(UserId userId);

    Optional<User> query(UserId userId);

    default User get(UserEmail email) {
        User user = query(email).orElse(null);
        Validator.notNull(user);
        return user;
    }

    default UserId getUserId(UserEmail email) {
        UserId userId = queryUserId(email).orElse(null);
        Validator.notNull(userId);
        return userId;
    }

    Optional<UserId> queryUserId(UserEmail email);

    Optional<User> query(UserEmail email);

    void add(User user);

    SumPagedRep<User> query(UserQuery userQuery);

    void remove(User user1);

    void batchLock(List<PatchCommand> commands);

    long countTotal();

    Set<UserId> getIds();

    void updateMfaInfo(MfaInfo mfaInfo, UserId userId);

    Optional<LoginUser> queryLoginUser(UserEmail email);

    Optional<LoginUser> queryLoginUser(UserId userId);

    default LoginUser getLoginUser(UserEmail email) {
        LoginUser user = queryLoginUser(email).orElse(null);
        Validator.notNull(user);
        return user;
    }

    default LoginUser getLoginUser(UserId userId) {
        LoginUser user = queryLoginUser(userId).orElse(null);
        Validator.notNull(user);
        return user;
    }
}