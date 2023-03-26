package com.mt.access.domain.model.user;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.Set;

public interface UserRelationRepository {
    void add(UserRelation userRelation);

    SumPagedRep<UserRelation> getByQuery(UserRelationQuery query);

    void remove(UserRelation e);

    SumPagedRep<UserRelation> getByUserId(UserId id);

    Optional<UserRelation> getByUserIdAndProjectId(UserId id, ProjectId projectId);

    Set<ProjectId> getProjectIds();

    long countProjectOwnedTotal(ProjectId projectId);

    long countProjectAdmin(RoleId adminRoleId);

    void removeAll(Set<UserRelation> allByQuery);

    Set<UserId> getUserIds();
}
