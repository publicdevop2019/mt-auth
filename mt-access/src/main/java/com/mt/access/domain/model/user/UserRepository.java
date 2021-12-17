package com.mt.access.domain.model.user;

import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> userOfId(UserId userId);

    void add(User user);

    Optional<User> searchExistingUserWith(UserEmail email);

    SumPagedRep<User> usersOfQuery(UserQuery userQuery);

    void remove(User user1);

    void batchLock(List<PatchCommand> commands);
}