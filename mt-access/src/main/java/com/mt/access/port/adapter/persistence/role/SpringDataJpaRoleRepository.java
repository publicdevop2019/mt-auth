package com.mt.access.port.adapter.persistence.role;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.RoleRepository;
import com.mt.access.domain.model.role.Role_;
import com.mt.access.port.adapter.persistence.BatchInsertKeyValue;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

public interface SpringDataJpaRoleRepository extends RoleRepository, JpaRepository<Role, Long> {

    default Role query(RoleId id) {
        return query(new RoleQuery(id)).findFirst().orElse(null);
    }

    default void add(Role role) {
        save(role);
    }

    default void addAll(Set<Role> roles) {
        List<Role> arrayList = new ArrayList<>(roles);
        CommonDomainRegistry.getJdbcTemplate()
            .batchUpdate("INSERT INTO role " +
                    "(" +
                    "id, " +
                    "created_at, " +
                    "created_by, " +
                    "modified_at, " +
                    "modified_by, " +
                    "version, " +
                    "name, " +
                    "description, " +
                    "parent_id, " +
                    "domain_id, " +
                    "project_id, " +
                    "system_create, " +
                    "tenant_id, " +
                    "type" +
                    ") VALUES " +
                    "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)", arrayList, roles.size(),
                (ps, role) -> {
                    ps.setLong(1, role.getId());
                    ps.setLong(2, Instant.now().toEpochMilli());
                    ps.setString(3, "NOT_HTTP");
                    ps.setLong(4, Instant.now().toEpochMilli());
                    ps.setString(5, "NOT_HTTP");
                    ps.setLong(6, 0L);
                    ps.setString(7, role.getName());
                    ps.setString(8, role.getDescription());
                    ps.setString(9, role.getParentId() == null ? null :
                        role.getParentId().getDomainId());
                    ps.setString(10, role.getRoleId().getDomainId());
                    ps.setString(11, role.getProjectId().getDomainId());
                    ps.setBoolean(12, role.getSystemCreate());
                    ps.setString(13, role.getTenantId() == null ? null :
                        role.getTenantId().getDomainId());
                    ps.setString(14, role.getType().name());
                });
        //for mapped tables
        List<BatchInsertKeyValue> commonPermList = new ArrayList<>();
        List<BatchInsertKeyValue> apiPermList = new ArrayList<>();
        List<BatchInsertKeyValue> extPermList = new ArrayList<>();
        roles.forEach(e -> {
            if (Checker.notNullOrEmpty(e.getCommonPermissionIds())) {
                List<BatchInsertKeyValue> collect = e.getCommonPermissionIds().stream()
                    .map(ee -> new BatchInsertKeyValue(e.getId(), ee.getDomainId())).collect(
                        Collectors.toList());
                commonPermList.addAll(collect);
            }
            if (Checker.notNullOrEmpty(e.getApiPermissionIds())) {
                List<BatchInsertKeyValue> collect = e.getApiPermissionIds().stream()
                    .map(ee -> new BatchInsertKeyValue(e.getId(), ee.getDomainId())).collect(
                        Collectors.toList());
                apiPermList.addAll(collect);
            }
            if (Checker.notNullOrEmpty(e.getExternalPermissionIds())) {
                List<BatchInsertKeyValue> collect = e.getExternalPermissionIds().stream()
                    .map(ee -> new BatchInsertKeyValue(e.getId(), ee.getDomainId())).collect(
                        Collectors.toList());
                extPermList.addAll(collect);
            }
        });
        if (commonPermList.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate("INSERT INTO role_common_permission_map " +
                        "(" +
                        "id, " +
                        "permission" +
                        ") VALUES " +
                        "(?,?)", commonPermList, commonPermList.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
        if (apiPermList.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate("INSERT INTO role_api_permission_map " +
                        "(" +
                        "id, " +
                        "permission" +
                        ") VALUES " +
                        "(?,?)", apiPermList, apiPermList.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
        if (extPermList.size() > 0) {
            CommonDomainRegistry.getJdbcTemplate()
                .batchUpdate("INSERT INTO role_external_permission_map " +
                        "(" +
                        "id, " +
                        "permission" +
                        ") VALUES " +
                        "(?,?)", extPermList, extPermList.size(),
                    (ps, perm) -> {
                        ps.setLong(1, perm.getId());
                        ps.setString(2, perm.getValue());
                    });
        }
    }

    default Optional<Role> queryClientRoot(ProjectId projectId) {
        return _queryClientRoot(projectId);
    }

    default void remove(Role role) {
        delete(role);
    }

    default SumPagedRep<Role> query(RoleQuery roleQuery) {
        return QueryBuilderRegistry.getRoleAdaptor().execute(roleQuery);
    }

    default Set<ProjectId> getProjectIds() {
        return getProjectId();
    }

    @Query("SELECT DISTINCT r.projectId FROM Role r")
    Set<ProjectId> getProjectId();

    @Query("SELECT r FROM Role r WHERE r.type = 'CLIENT_ROOT' AND r.projectId = ?1")
    Optional<Role> _queryClientRoot(ProjectId projectId);

    @Query("SELECT count(*) FROM Role r WHERE r.projectId = ?1 AND r.type = 'USER' ")
    long countProjectCreateTotal_(ProjectId projectId);

    default long countProjectCreateTotal(ProjectId projectId) {
        return countProjectCreateTotal_(projectId);
    }

    @Component
    class JpaCriteriaApiRoleAdaptor {
        public SumPagedRep<Role> execute(RoleQuery query) {
            if (query.getReferredPermissionId() != null) {
                return permissionSearch(query);
            }
            QueryUtility.QueryContext<Role> queryContext =
                QueryUtility.prepareContext(Role.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Role_.ROLE_ID, queryContext));
            Optional.ofNullable(query.getParentId()).ifPresent(e -> QueryUtility
                .addDomainIdIsPredicate(e.getDomainId(), Role_.PARENT_ID, queryContext));
            Optional.ofNullable(query.getParentIdNull()).ifPresent(e -> QueryUtility
                .addDomainIdIsNullPredicate(Role_.PARENT_ID, queryContext));
            Optional.ofNullable(query.getProjectIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Role_.PROJECT_ID, queryContext));
            Optional.ofNullable(query.getNames())
                .ifPresent(e -> QueryUtility.addStringInPredicate(e, Role_.NAME, queryContext));
            Optional.ofNullable(query.getTypes()).ifPresent(
                e -> QueryUtility.addEnumLiteralEqualPredicate(e, Role_.TYPE, queryContext));
            Optional.ofNullable(query.getTenantIds()).ifPresent(
                e -> QueryUtility.addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Role_.TENANT_ID, queryContext));
            Order order = null;
            if (Checker.isTrue(query.getSort().getById())) {
                order = QueryUtility
                    .getDomainIdOrder(Role_.ROLE_ID, queryContext, query.getSort().getIsAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }

        private SumPagedRep<Role> permissionSearch(RoleQuery query) {
            String domainId = query.getReferredPermissionId().getDomainId();
            PageConfig pageConfig = query.getPageConfig();
            EntityManager entityManager = QueryUtility.getEntityManager();
            String countSql =
                "SELECT COUNT(r.id) from `role` r WHERE r.id IN (SELECT rapm.id FROM role_api_permission_map rapm WHERE rapm.permission = :domainId)" +
                    " OR r.id IN (SELECT rcpm.id FROM role_common_permission_map rcpm WHERE rcpm.permission = :domainId)" +
                    " OR r.id IN (SELECT repm.id FROM role_external_permission_map repm WHERE repm.permission = :domainId)";
            javax.persistence.Query countQuery = entityManager.createNativeQuery(
                countSql);
            countQuery.setParameter("domainId", domainId);
            String sql =
                "SELECT r.* from `role` r WHERE r.id IN (SELECT rapm.id FROM role_api_permission_map rapm WHERE rapm.permission = :domainId)" +
                    " OR r.id IN (SELECT rcpm.id FROM role_common_permission_map rcpm WHERE rcpm.permission = :domainId)" +
                    " OR r.id IN (SELECT repm.id FROM role_external_permission_map repm WHERE repm.permission = :domainId) LIMIT :limit OFFSET :offset";
            javax.persistence.Query findQuery = entityManager.createNativeQuery(
                sql,
                Role.class);
            findQuery.setParameter("domainId", domainId);
            findQuery.setParameter("limit", pageConfig.getPageSize());
            findQuery.setParameter("offset", pageConfig.getOffset());
            long count = ((Number) countQuery.getSingleResult()).longValue();
            List<Role> resultList = findQuery.getResultList();
            return new SumPagedRep<>(resultList, count);
        }

    }


}
