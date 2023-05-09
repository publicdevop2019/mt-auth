package com.mt.access.port.adapter.persistence.permission;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.permission.PermissionRepository;
import com.mt.access.domain.model.permission.Permission_;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

public interface SpringDataJpaPermissionRepository
    extends PermissionRepository, JpaRepository<Permission, Long> {

    default Permission query(PermissionId id) {
        return query(new PermissionQuery(id)).findFirst().orElse(null);
    }

    default void add(Permission permission) {
        save(permission);
    }

    default void remove(Permission permission) {
        delete(permission);
    }

    default void removeAll(Set<Permission> permissions) {
        deleteAll(permissions);
    }

    default Set<EndpointId> allApiPermissionLinkedEpId() {
        return allApiPermissionLinkedEpId_();
    }

    default Set<PermissionId> allPermissionId() {
        return allPermissionId_();
    }

    default Set<PermissionId> getLinkedApiPermissionFor(Set<PermissionId> e) {
        return getLinkedApiPermissionFor_(e);
    }

    ;

    @Query("select distinct p.name from Permission p where p.type='API' and p.parentId != null")
    Set<EndpointId> allApiPermissionLinkedEpId_();

    @Query("select c from Permission p join p.linkedApiPermissionIds c where p.permissionId in ?1")
    Set<PermissionId> getLinkedApiPermissionFor_(Set<PermissionId> e);

    @Query("select distinct p.permissionId from Permission p")
    Set<PermissionId> allPermissionId_();

    @Query("select count(*) from Permission p where p.projectId = ?1 and p.type = 'COMMON' and p.parentId != null")
    long countProjectCreateTotal_(ProjectId projectId);

    default SumPagedRep<Permission> query(PermissionQuery permissionQuery) {
        return QueryBuilderRegistry.getPermissionAdaptor().execute(permissionQuery);
    }

    default long countProjectCreateTotal(ProjectId projectId) {
        return countProjectCreateTotal_(projectId);
    }

    @Component
    class JpaCriteriaApiPermissionAdaptor {
        public SumPagedRep<Permission> execute(PermissionQuery query) {
            QueryUtility.QueryContext<Permission> queryContext =
                QueryUtility.prepareContext(Permission.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Permission_.PERMISSION_ID, queryContext));
            Optional.ofNullable(query.getParentId()).ifPresent(e -> QueryUtility
                .addDomainIdIsPredicate(e.getDomainId(), Permission_.PARENT_ID, queryContext));
            Optional.ofNullable(query.getParentIdNull()).ifPresent(e -> QueryUtility
                .addDomainIdIsNullPredicate(Permission_.PARENT_ID, queryContext));
            Optional.ofNullable(query.getProjectIds())
                .ifPresent(e -> QueryUtility.addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Permission_.PROJECT_ID, queryContext));
            Optional.ofNullable(query.getTenantIds()).ifPresent(e -> {
                QueryUtility
                    .addDomainIdInPredicate(
                        e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                        Permission_.TENANT_ID, queryContext);
            });
            Optional.ofNullable(query.getNames()).ifPresent(
                e -> QueryUtility.addStringInPredicate(e, Permission_.NAME, queryContext));
            Optional.ofNullable(query.getShared()).ifPresent(
                e -> QueryUtility.addBooleanEqualPredicate(e, Permission_.SHARED, queryContext));
            Optional.ofNullable(query.getTypes()).ifPresent(e -> {
                QueryUtility.addEnumLiteralEqualPredicate(e, Permission_.TYPE, queryContext);
            });
            Order order = null;
            if (query.getSort().isById()) {
                order = QueryUtility.getDomainIdOrder(Permission_.PERMISSION_ID, queryContext,
                    query.getSort().isAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }
    }

}
