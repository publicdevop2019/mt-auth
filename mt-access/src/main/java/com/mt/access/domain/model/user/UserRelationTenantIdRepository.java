package com.mt.access.domain.model.user;

import com.mt.access.domain.model.project.ProjectId;
import java.util.Set;

public interface UserRelationTenantIdRepository {
    Set<ProjectId> query(UserRelation userRelation);

    void add(UserRelation userRelation, ProjectId projectId);

    void removeAll(UserRelation userRelation, Set<ProjectId> projectIds);

    void remove(UserRelation userRelation, ProjectId projectId);
}
