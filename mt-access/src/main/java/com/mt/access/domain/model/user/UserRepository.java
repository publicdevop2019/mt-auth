package com.mt.access.domain.model.user;

import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    Optional<User> by(UserId userId);

    Optional<User> by(UserEmail email);

    void add(User user);

    SumPagedRep<User> query(UserQuery userQuery);

    void remove(User user1);

    void batchLock(List<PatchCommand> commands);

    long countTotal();

    Set<UserId> getIds();
}