package com.mt.access.port.adapter.persistence.role;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.*;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface SpringDataJpaRoleRepository extends RoleRepository, JpaRepository<Role, Long> {

    default Optional<Role> getById(RoleId id) {
        return getByQuery(new RoleQuery(id)).findFirst();
    }

    default void add(Role role) {
        save(role);
    }

    default void remove(Role Role) {
        Role.softDelete();
        save(Role);
    }

    default SumPagedRep<Role> getByQuery(RoleQuery roleQuery) {
        return QueryBuilderRegistry.getRoleAdaptor().execute(roleQuery);
    }

    default Set<ProjectId> getProjectIds() {
        return getProjectId();
    }

    @Query("select distinct ep.projectId from Role ep")
    Set<ProjectId> getProjectId();

    @Component
    class JpaCriteriaApiRoleAdaptor {
        public SumPagedRep<Role> execute(RoleQuery query) {
            QueryUtility.QueryContext<Role> queryContext = QueryUtility.prepareContext(Role.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                    .addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Role_.ROLE_ID, queryContext));
            Optional.ofNullable(query.getParentId()).ifPresent(e -> QueryUtility.addDomainIdIsPredicate(e.getDomainId(), Role_.PARENT_ID, queryContext));
            Optional.ofNullable(query.getProjectIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Role_.PROJECT_ID, queryContext));
            Optional.ofNullable(query.getExternalPermissionIds()).ifPresent(e -> QueryUtility.addStringLikePredicate(e.getDomainId(), Role_.EXTERNAL_PERMISSION_IDS, queryContext));
            Optional.ofNullable(query.getNames()).ifPresent(e -> QueryUtility.addStringInPredicate(e, Role_.NAME, queryContext));
            Optional.ofNullable(query.getTypes()).ifPresent(e -> QueryUtility.addEnumLiteralEqualPredicate(e,Role_.TYPE,queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getDomainIdOrder(Role_.ROLE_ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }


}
