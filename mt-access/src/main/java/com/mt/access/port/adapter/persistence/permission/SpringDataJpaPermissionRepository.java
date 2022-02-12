package com.mt.access.port.adapter.persistence.permission;

import com.mt.access.domain.model.permission.*;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleType;
import com.mt.access.domain.model.role.Role_;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.access.port.adapter.persistence.role.SpringDataJpaRoleRepository;
import com.mt.common.CommonConstant;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.data.jpa.repository.JpaRepository;
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
            Optional.ofNullable(query.getProjectIds())
                    .ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Permission_.PROJECT_ID, queryContext));
            Optional.ofNullable(query.getTenantIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Permission_.TENANT_ID, queryContext));
            Optional.ofNullable(query.getNames()).ifPresent(e -> QueryUtility.addStringInPredicate(e, Permission_.NAME, queryContext));
            Optional.ofNullable(query.getTypes()).ifPresent(e -> {
                queryContext.getPredicates().add(PermissionTypePredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot(),query.isTypesIsAndRelation()));
                Optional.ofNullable(queryContext.getCountPredicates())
                        .ifPresent(ee -> ee.add(PermissionTypePredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getCountRoot(),query.isTypesIsAndRelation())));
            });
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getDomainIdOrder(Permission_.PERMISSION_ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
        //@todo simple enum query
        private static class PermissionTypePredicateConverter {
            public static Predicate getPredicate(Set<PermissionType> query, CriteriaBuilder cb, Root<Permission> root, boolean isAnd) {
                if (query.size()>1) {
                    List<Predicate> list2 = new ArrayList<>();
                    for (PermissionType str : query) {
                        if (PermissionType.API_ROOT.equals(str)) {
                            list2.add(cb.like(root.get(Permission_.TYPE).as(String.class), "%" + PermissionType.API_ROOT.name() + "%"));
                        } else if (PermissionType.API.equals(str)) {
                            list2.add(cb.like(root.get(Permission_.TYPE).as(String.class), "%" + PermissionType.API.name() + "%"));
                        } else if (PermissionType.COMMON.equals(str)) {
                            list2.add(cb.like(root.get(Permission_.TYPE).as(String.class), "%" + PermissionType.COMMON.name() + "%"));
                        } else if (PermissionType.PROJECT.equals(str)) {
                            list2.add(cb.like(root.get(Permission_.TYPE).as(String.class), "%" + PermissionType.PROJECT.name() + "%"));
                        }
                    }
                    if(isAnd){
                        return cb.and(list2.toArray(Predicate[]::new));
                    }
                    return cb.or(list2.toArray(Predicate[]::new));
                } else {
                    return getExpression(query.stream().findFirst().get(), cb, root);
                }
            }

            private static Predicate getExpression(PermissionType str, CriteriaBuilder cb, Root<Permission> root) {
                if (PermissionType.API.equals(str)) {
                    return cb.like(root.get(Permission_.TYPE).as(String.class), "%" + PermissionType.API.name() + "%");
                } else if (PermissionType.PROJECT.equals(str)) {
                    return cb.like(root.get(Permission_.TYPE).as(String.class), "%" + PermissionType.PROJECT.name() + "%");
                } else if (PermissionType.API_ROOT.equals(str)) {
                    return cb.like(root.get(Permission_.TYPE).as(String.class), "%" + PermissionType.API_ROOT.name() + "%");
                } else if (PermissionType.COMMON.equals(str)) {
                    return cb.like(root.get(Permission_.TYPE).as(String.class), "%" + PermissionType.COMMON.name() + "%");
                } else {
                    return null;
                }
            }
        }
    }
    //@tod
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
