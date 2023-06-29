package com.mt.access.port.adapter.persistence.role;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.RoleRepository;
import com.mt.access.domain.model.role.Role_;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
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

    default void remove(Role role) {
        delete(role);
    }

    default SumPagedRep<Role> query(RoleQuery roleQuery) {
        return QueryBuilderRegistry.getRoleAdaptor().execute(roleQuery);
    }

    default Set<ProjectId> getProjectIds() {
        return getProjectId();
    }

    @Query("select distinct ep.projectId from Role ep")
    Set<ProjectId> getProjectId();

    @Query("select count(*) from Role r where r.projectId = ?1 and r.type = 'USER' ")
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
