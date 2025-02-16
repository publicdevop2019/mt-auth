package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.validate.Validator;
import java.util.Set;

public class UserRelationTenantId {
    public static void add(UserRelation userRelation, ProjectId tenantId) {
        Validator.notNull(tenantId);
        DomainRegistry.getUserRelationTenantIdRepository()
            .add(userRelation, tenantId);
    }

    public static void removeAll(UserRelation relation, Set<ProjectId> oldTenantIds) {
        DomainRegistry.getUserRelationTenantIdRepository().removeAll(relation, oldTenantIds);
    }
}
