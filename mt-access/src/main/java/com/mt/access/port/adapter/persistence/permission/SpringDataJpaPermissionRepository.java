package com.mt.access.port.adapter.persistence.permission;

import com.mt.access.domain.model.endpoint.Endpoint_;
import com.mt.access.domain.model.permission.*;
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

public interface SpringDataJpaPermissionRepository extends PermissionRepository, JpaRepository<Permission, Long> {
    default Optional<Permission> getById(PermissionId id) {
        return getByQuery(new PermissionQuery(id)).findFirst();
    }

    default void add(Permission Permission) {
        save(Permission);
    }

    default void remove(Permission Permission) {
        Permission.setDeleted(true);
        save(Permission);
    }

    default SumPagedRep<Permission> getByQuery(PermissionQuery PermissionQuery) {
        return QueryBuilderRegistry.getPermissionAdaptor().execute(PermissionQuery);
    }

    @Component
    class JpaCriteriaApiPermissionAdaptor {
        public SumPagedRep<Permission> execute(PermissionQuery query) {
            QueryUtility.QueryContext<Permission> queryContext = QueryUtility.prepareContext(Permission.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                    .addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Permission_.PERMISSION_ID, queryContext));
            Optional.ofNullable(query.getParentId()).ifPresent(e -> addParentIdPredicate(query.getParentId().getDomainId(), Permission_.PARENT_ID, queryContext));
            Optional.ofNullable(query.getProjectIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Permission_.PROJECT_ID, queryContext));
            Optional.ofNullable(query.getTenantIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Permission_.TENANT_ID, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getDomainIdOrder(Permission_.PERMISSION_ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }

    private static <T> void addParentIdPredicate(String value, String sqlFieldName, QueryUtility.QueryContext<T> context) {
        if("null".equalsIgnoreCase(value)){
            context.getPredicates().add(context.getCriteriaBuilder().isNull(context.getRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class)));
            Optional.ofNullable(context.getCountPredicates()).ifPresent(e -> e.add(context.getCriteriaBuilder().isNull(context.getCountRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class))));

        }else{
            context.getPredicates().add(context.getCriteriaBuilder().equal(context.getRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class), value));
            Optional.ofNullable(context.getCountPredicates()).ifPresent(e -> e.add(context.getCriteriaBuilder().equal(context.getCountRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID).as(String.class), value)));

        }
    }
}
