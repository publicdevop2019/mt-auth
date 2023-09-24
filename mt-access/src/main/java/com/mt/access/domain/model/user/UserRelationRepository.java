package com.mt.access.domain.model.user;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Optional;
import java.util.Set;

public interface UserRelationRepository {
    void add(UserRelation userRelation);

    SumPagedRep<UserRelation> query(UserRelationQuery query);

    void remove(UserRelation e);

    SumPagedRep<UserRelation> get(UserId id);

    default UserRelation get(UserId id, ProjectId projectId){
        UserRelation userRelation = query(id, projectId).orElse(null);
        Validator.notNull(userRelation);
        return userRelation;
    }

    Optional<UserRelation> query(UserId id, ProjectId projectId);

    Set<ProjectId> getProjectIds();

    long countProjectOwnedTotal(ProjectId projectId);

    long countProjectAdmin(RoleId adminRoleId);

    void removeAll(Set<UserRelation> allByQuery);

    Set<UserId> getUserIds();

    void update(UserRelation relation, UserRelation userRelation);
}
