package com.mt.access.port.adapter.persistence.system_role;

import com.mt.access.domain.model.system_role.SystemRole;
import com.mt.access.domain.model.system_role.SystemRoleQuery;
import com.mt.access.domain.model.system_role.SystemRoleRepository;
import com.mt.access.domain.model.system_role.SystemRole_;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Order;
import java.util.Optional;
import java.util.stream.Collectors;

public interface SpringDataJpaSystemRoleRepository extends SystemRoleRepository, JpaRepository<SystemRole, Long> {
    default SumPagedRep<SystemRole> systemRoleOfQuery(SystemRoleQuery var0) {
        return QueryBuilderRegistry.getSystemRoleAdaptor().execute(var0);
    }

    default void add(SystemRole var0) {
        save(var0);
    }
    default void remove(SystemRole e){
        e.setDeleted(true);
        save(e);
    };
    @Component
    class JpaCriteriaApiSystemRoleAdaptor {
        public SumPagedRep<SystemRole> execute(SystemRoleQuery query) {
            QueryUtility.QueryContext<SystemRole> queryContext = QueryUtility.prepareContext(SystemRole.class, query);
            Optional.ofNullable(query.getNames()).ifPresent(e -> QueryUtility.addStringInPredicate(e, SystemRole_.NAME, queryContext));
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), SystemRole_.ROLE_ID, queryContext));
            Optional.ofNullable(query.getType()).ifPresent(e -> QueryUtility.addStringEqualPredicate(e.name(), SystemRole_.ROLE_TYPE, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getDomainIdOrder(SystemRole_.ROLE_ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }

    }
}
