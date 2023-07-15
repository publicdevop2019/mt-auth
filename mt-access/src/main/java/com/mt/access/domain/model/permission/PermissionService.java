package com.mt.access.domain.model.permission;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    public Set<PermissionId> tenantFindPermissionIds(List<String> endpointIds,
                                                     Set<ProjectId> projectIds) {
        Set<PermissionId> linkedPermId = null;
        if (endpointIds != null && !endpointIds.isEmpty()) {
            Validator.lessThanOrEqualTo(endpointIds, 20);
            Set<EndpointId> collect =
                endpointIds.stream().map(EndpointId::new)
                    .collect(Collectors.toSet());
            Set<Endpoint> allByQuery = QueryUtility.getAllByQuery(
                e -> DomainRegistry.getEndpointRepository().query(e),
                EndpointQuery.tenantQuery(collect, projectIds));
            linkedPermId = allByQuery.stream().map(Endpoint::getPermissionId)
                .collect(Collectors.toSet());
            Validator.sizeEquals(endpointIds, linkedPermId);
        }
        return linkedPermId;
    }

    public void cleanRelated(PermissionId permissionId, TransactionContext context) {
        //clean endpoint's permission entity
        Permission permission = DomainRegistry.getPermissionRepository().get(permissionId);
        permission.secureEndpointRemoveCleanUp(context);
        //clean linked api permission
        //TODO this is more effective with direct SQL query
        Set<Permission> allByQuery =
            QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository()
                .query(e), PermissionQuery.linkedApiPermission(permissionId));
        allByQuery.forEach(e-> e.removeApiPermission(permissionId));
    }
}
