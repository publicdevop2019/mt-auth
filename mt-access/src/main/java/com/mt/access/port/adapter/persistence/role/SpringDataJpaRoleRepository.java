package com.mt.access.port.adapter.persistence.role;

import com.mt.access.domain.model.role.*;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.CommonConstant;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Order;
import java.util.Optional;
import java.util.stream.Collectors;

public interface SpringDataJpaRoleRepository extends RoleRepository, JpaRepository<Role, Long> {
    //@todo add to common
    private static <T> void addParentIdPredicate(String value, String sqlFieldName, QueryUtility.QueryContext<T> context) {
        if ("null".equalsIgnoreCase(value)) {
            context.getPredicates().add(context.getCriteriaBuilder().isNull(context.getRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class)));
            Optional.ofNullable(context.getCountPredicates()).ifPresent(e -> e.add(context.getCriteriaBuilder().isNull(context.getCountRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class))));

        } else {
            context.getPredicates().add(context.getCriteriaBuilder().equal(context.getRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class), value));
            Optional.ofNullable(context.getCountPredicates()).ifPresent(e -> e.add(context.getCriteriaBuilder().equal(context.getCountRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class), value)));

        }
    }

    default Optional<Role> getById(RoleId id) {
        return getByQuery(new RoleQuery(id)).findFirst();
    }

    default void add(Role role) {
        save(role);
    }

    default void remove(Role Role) {
        Role.setDeleted(true);
        save(Role);
    }

    default SumPagedRep<Role> getByQuery(RoleQuery roleQuery) {
        return QueryBuilderRegistry.getRoleAdaptor().execute(roleQuery);
    }

    @Component
    class JpaCriteriaApiRoleAdaptor {
        public SumPagedRep<Role> execute(RoleQuery query) {
            QueryUtility.QueryContext<Role> queryContext = QueryUtility.prepareContext(Role.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                    .addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Role_.ROLE_ID, queryContext));
            Optional.ofNullable(query.getParentId()).ifPresent(e -> addParentIdPredicate(query.getParentId().getDomainId(), Role_.PARENT_ID, queryContext));
            Optional.ofNullable(query.getProjectIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Role_.PROJECT_ID, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getDomainIdOrder(Role_.ROLE_ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
