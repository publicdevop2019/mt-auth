package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.develop.Analytics;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PermissionCheckService {
    public void canAccess(Set<ProjectId> ids, String permissionName) {
        Validator.notNull(ids);
        Analytics permissionAnalytics = Analytics.start(Analytics.Type.PERMISSION_CHECK);
        if (ids == null) {
            throw new DefinedRuntimeException("no project id found", "1027",
                HttpResponseCode.FORBIDDEN);
        }
        Set<ProjectId> collect = ids.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (collect.size() == 0) {
            throw new DefinedRuntimeException("no project id found", "1028",
                HttpResponseCode.FORBIDDEN);
        }
        //first check access to tenant project, query projectId must be one of jwt tenant ids
        Set<ProjectId> authorizedTenantId = DomainRegistry.getCurrentUserService().getTenantIds();
        boolean b = authorizedTenantId.containsAll(ids);
        if (!b) {
            throw new DefinedRuntimeException("not allowed project", "1029",
                HttpResponseCode.FORBIDDEN);
        }
        //second check if it has read client access to current project
        PermissionQuery permissionQuery = PermissionQuery
            .ofProjectWithTenantIds(ids, permissionName);
        Set<PermissionId> idSet = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getPermissionRepository().queryPermissionId(e),
                permissionQuery);
        boolean hasPermissions =
            DomainRegistry.getCurrentUserService().getPermissionIds().containsAll(idSet);
        permissionAnalytics.stop();
        if (!hasPermissions) {
            throw new DefinedRuntimeException("no required access permission: " + permissionName,
                "1030",
                HttpResponseCode.FORBIDDEN);
        }
    }

    public void canAccess(ProjectId id, String permissionName) {
        Validator.notNull(id);
        canAccess(Collections.singleton(id), permissionName);
    }

    public void sameCreatedBy(Auditable e) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        if (!new UserId(e.getCreatedBy()).equals(userId)) {
            throw new DefinedRuntimeException("not created by same user", "1031",
                HttpResponseCode.FORBIDDEN);
        }
    }
}
