package com.mt.access.port.adapter.persistence.role;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientType;
import com.mt.access.domain.model.client.Client_;
import com.mt.access.domain.model.role.*;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.access.port.adapter.persistence.client.SpringDataJpaClientRepository;
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
import java.util.*;
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
        Role.softDelete();
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
            Optional.ofNullable(query.getExternalPermissionIds()).ifPresent(e -> QueryUtility.addStringLikePredicate(e.getDomainId(), Role_.EXTERNAL_PERMISSION_IDS, queryContext));
            Optional.ofNullable(query.getNames()).ifPresent(e -> QueryUtility.addStringInPredicate(e, Role_.NAME, queryContext));
            Optional.ofNullable(query.getTypes()).ifPresent(e -> {
                queryContext.getPredicates().add(JpaCriteriaApiRoleAdaptor.RoleTypePredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot(),query.isTypesIsAndRelation()));
                Optional.ofNullable(queryContext.getCountPredicates())
                        .ifPresent(ee -> ee.add(JpaCriteriaApiRoleAdaptor.RoleTypePredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getCountRoot(),query.isTypesIsAndRelation())));
            });
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getDomainIdOrder(Role_.ROLE_ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
        private static class RoleTypePredicateConverter {
            public static Predicate getPredicate(Set<RoleType> query, CriteriaBuilder cb, Root<Role> root,boolean isAnd) {
                if (query.size()>1) {
                    List<Predicate> list2 = new ArrayList<>();
                    for (RoleType str : query) {
                        if (RoleType.CLIENT_ROOT.equals(str)) {
                            list2.add(cb.like(root.get(Role_.TYPE).as(String.class), "%" + RoleType.CLIENT_ROOT.name() + "%"));
                        } else if (RoleType.PROJECT.equals(str)) {
                            list2.add(cb.like(root.get(Role_.TYPE).as(String.class), "%" + RoleType.PROJECT.name() + "%"));
                        } else if (RoleType.CLIENT.equals(str)) {
                            list2.add(cb.like(root.get(Role_.TYPE).as(String.class), "%" + RoleType.CLIENT.name() + "%"));
                        } else if (RoleType.USER.equals(str)) {
                            list2.add(cb.like(root.get(Role_.TYPE).as(String.class), "%" + RoleType.USER.name() + "%"));
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

            private static Predicate getExpression(RoleType str, CriteriaBuilder cb, Root<Role> root) {
                if (RoleType.CLIENT_ROOT.equals(str)) {
                    return cb.like(root.get(Role_.TYPE).as(String.class), "%" + RoleType.CLIENT_ROOT.name() + "%");
                } else if (RoleType.PROJECT.equals(str)) {
                    return cb.like(root.get(Role_.TYPE).as(String.class), "%" + RoleType.PROJECT.name() + "%");
                } else if (RoleType.CLIENT.equals(str)) {
                    return cb.like(root.get(Role_.TYPE).as(String.class), "%" + RoleType.CLIENT.name() + "%");
                } else if (RoleType.USER.equals(str)) {
                    return cb.like(root.get(Role_.TYPE).as(String.class), "%" + RoleType.USER.name() + "%");
                } else {
                    return null;
                }
            }
        }
    }


}
