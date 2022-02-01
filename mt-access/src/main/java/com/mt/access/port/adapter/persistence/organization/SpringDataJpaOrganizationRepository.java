package com.mt.access.port.adapter.persistence.organization;

import com.mt.access.domain.model.organization.*;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Order;
import java.util.Optional;
import java.util.stream.Collectors;

public interface SpringDataJpaOrganizationRepository extends OrganizationRepository, JpaRepository<Organization, Long> {
    default Optional<Organization> getById(OrganizationId id) {
        return getByQuery(new OrganizationQuery(id)).findFirst();
    }

    default void add(Organization Organization) {
        save(Organization);
    }

    default void remove(Organization Organization) {
        Organization.setDeleted(true);
        save(Organization);
    }

    default SumPagedRep<Organization> getByQuery(OrganizationQuery OrganizationQuery) {
        return QueryBuilderRegistry.getOrganizationAdaptor().execute(OrganizationQuery);
    }

    @Component
    class JpaCriteriaApiOrganizationAdaptor {
        public SumPagedRep<Organization> execute(OrganizationQuery query) {
            QueryUtility.QueryContext<Organization> queryContext = QueryUtility.prepareContext(Organization.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                    .addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Organization_.ORGANIZATION_ID, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getDomainIdOrder(Organization_.ORGANIZATION_ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
