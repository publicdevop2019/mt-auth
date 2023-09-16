package com.mt.access.port.adapter.persistence.permission;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.permission.PermissionRepository;
import com.mt.access.domain.model.permission.Permission_;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

public interface SpringDataJpaPermissionRepository
    extends PermissionRepository, JpaRepository<Permission, Long> {

    default Permission query(PermissionId id) {
        return query(new PermissionQuery(id)).findFirst().orElse(null);
    }

    default SumPagedRep<PermissionId> queryPermissionId(PermissionQuery query) {
        String inSql = String.join(",", Collections.nCopies(query.getTenantIds().size(), "?"));
        String inSql2 = String.join(",", Collections.nCopies(query.getNames().size(), "?"));
        List<Object> dataArgs = new ArrayList<>();
        dataArgs.addAll(
            query.getTenantIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
        dataArgs.addAll(query.getNames());
        dataArgs.add(query.getPageConfig().getPageSize());
        dataArgs.add(query.getPageConfig().getOffset());
        Object data = CommonDomainRegistry.getJdbcTemplate()
            .query(
                String.format("SELECT p.domain_id FROM permission p " +
                    "WHERE p.tenant_id IN (%s) " +
                    "AND p.name IN (%s) " +
                    "ORDER BY p.id ASC " +
                    "LIMIT ? " +
                    "OFFSET ?", inSql, inSql2),
                dataArgs.toArray(),
                new ResultSetExtractor<Object>() {
                    @Override
                    public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                        if (!rs.next()) {
                            return Collections.emptyList();
                        }
                        List<PermissionId> permissionIds = new ArrayList<>();
                        do {
                            permissionIds.add(new PermissionId(rs.getString("domain_id")));
                        } while (rs.next());
                        return permissionIds;
                    }
                });
        List<Object> countArgs = new ArrayList<>();
        countArgs.addAll(
            query.getTenantIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
        countArgs.addAll(query.getNames());
        Long count = (Long) CommonDomainRegistry.getJdbcTemplate()
            .query(
                String.format("SELECT COUNT(*) AS count FROM permission p " +
                    "WHERE p.tenant_id IN (%s) " +
                    "AND p.name IN (%s)", inSql, inSql2),
                countArgs.toArray(),
                new ResultSetExtractor<Object>() {
                    @Override
                    public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                        if (!rs.next()) {
                            return 0L;
                        }else{
                            return rs.getLong("count");
                        }
                    }
                });
        return new SumPagedRep<>((List<PermissionId>) data, count);
    }

    default void add(Permission permission) {
        save(permission);
    }

    default void addAll(Set<Permission> permissions) {
        List<Permission> arrayList = new ArrayList<>(permissions);
        CommonDomainRegistry.getJdbcTemplate()
            .batchUpdate("INSERT INTO permission " +
                    "(" +
                    "id, " +
                    "created_at, " +
                    "created_by, " +
                    "modified_at, " +
                    "modified_by, " +
                    "version, " +
                    "name, " +
                    "parent_id, " +
                    "domain_id, " +
                    "project_id, " +
                    "shared, " +
                    "system_create, " +
                    "tenant_id, " +
                    "type" +
                    ") VALUES " +
                    "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)", arrayList, permissions.size(),
                (ps, permission) -> {
                    ps.setLong(1, permission.getId());
                    ps.setLong(2, Instant.now().toEpochMilli());
                    ps.setString(3, "NOT_HTTP");
                    ps.setLong(4, Instant.now().toEpochMilli());
                    ps.setString(5, "NOT_HTTP");
                    ps.setLong(6, 0L);
                    ps.setString(7, permission.getName());
                    ps.setString(8, permission.getParentId() == null ? null :
                        permission.getParentId().getDomainId());
                    ps.setString(9, permission.getPermissionId().getDomainId());
                    ps.setString(10, permission.getProjectId().getDomainId());
                    ps.setBoolean(11, permission.getShared());
                    ps.setBoolean(12, permission.getSystemCreate());
                    ps.setString(13, permission.getTenantId() == null ? null :
                        permission.getTenantId().getDomainId());
                    ps.setString(14, permission.getType().name());
                });
        //for linked tables
        List<BatchInsertKeyValue> linkedPermList = new ArrayList<>();
        permissions.forEach(e -> {
            if (Checker.notNullOrEmpty(e.getLinkedApiPermissionIds())) {
                List<BatchInsertKeyValue> collect = e.getLinkedApiPermissionIds().stream()
                    .map(ee -> new BatchInsertKeyValue(e.getId(), ee.getDomainId())).collect(
                        Collectors.toList());
                linkedPermList.addAll(collect);
            }
        });
        CommonDomainRegistry.getJdbcTemplate()
            .batchUpdate("INSERT INTO linked_permission_ids_map " +
                    "(" +
                    "id, " +
                    "domain_id" +
                    ") VALUES " +
                    "(?,?)", linkedPermList, linkedPermList.size(),
                (ps, permission) -> {
                    ps.setLong(1, permission.getId());
                    ps.setString(2, permission.getValue());
                });
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

    @Query("select distinct p.name from Permission p where p.type='API' and p.parentId != null")
    Set<EndpointId> allApiPermissionLinkedEpId_();

    @Query("select c from Permission p join p.linkedApiPermissionIds c where p.permissionId in ?1")
    Set<PermissionId> getLinkedApiPermissionFor_(Set<PermissionId> e);


    @Query("select distinct p.permissionId from Permission p")
    Set<PermissionId> allPermissionId_();

    @Query("select count(*) from Permission p where p.projectId = ?1 and p.type = 'COMMON' and p.parentId != null")
    long countProjectCreateTotal_(ProjectId projectId);

    default SumPagedRep<Permission> query(PermissionQuery permissionQuery) {
        if (Checker.notNullOrEmpty(permissionQuery.getLinkedApiPermissionIds())) {
            return getPermissionUsingApiPermission(permissionQuery);
        }
        return QueryBuilderRegistry.getPermissionAdaptor().execute(permissionQuery);
    }

    default SumPagedRep<Permission> getPermissionUsingApiPermission(PermissionQuery query) {
        Set<String> ids = query.getLinkedApiPermissionIds().stream().map(
            DomainId::getDomainId).collect(
            Collectors.toSet());
        EntityManager entityManager = QueryUtility.getEntityManager();
        javax.persistence.Query countQuery = entityManager.createNativeQuery(
            "SELECT COUNT(DISTINCT p.id) FROM permission p LEFT JOIN linked_permission_ids_map m ON p.id = m.id " +
                "WHERE m.domain_id IN :ids");
        countQuery.setParameter("ids", ids);
        javax.persistence.Query nativeQuery = entityManager.createNativeQuery(
            "SELECT p.* FROM permission AS p LEFT JOIN linked_permission_ids_map m ON p.id = m.id " +
                "WHERE m.domain_id IN :ids LIMIT :limit OFFSET :offset", Permission.class);
        nativeQuery.setParameter("ids", ids);
        nativeQuery.setParameter("limit", query.getPageConfig().getPageSize());
        nativeQuery.setParameter("offset", query.getPageConfig().getOffset());
        long count = ((Number) countQuery.getSingleResult()).longValue();
        List<Permission> resultList = nativeQuery.getResultList();
        return new SumPagedRep<>(resultList, count);
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
            if (Checker.isTrue(query.getSort().getById())) {
                order = QueryUtility.getDomainIdOrder(Permission_.PERMISSION_ID, queryContext,
                    query.getSort().getIsAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }
    }

}
