package com.mt.access.port.adapter.persistence.cors_profile;

import com.mt.access.domain.model.cors_profile.*;
import com.mt.access.domain.model.system_role.SystemRole;
import com.mt.access.domain.model.system_role.SystemRoleQuery;
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

public interface SpringDataJpaCorsProfileRepository extends CORSProfileRepository, JpaRepository<CORSProfile, Long> {
    default Optional<CORSProfile> corsProfileOfId(CORSProfileId id){
        return corsProfileOfQuery(new CORSProfileQuery(id)).findFirst();
    };

    default void add(CORSProfile corsProfile){
        save(corsProfile);
    };

    default void remove(CORSProfile corsProfile){
        corsProfile.setDeleted(true);
        save(corsProfile);
    };

    default SumPagedRep<CORSProfile> corsProfileOfQuery(CORSProfileQuery query){
        return QueryBuilderRegistry.getCorsProfileAdaptor().execute(query);
    };
    @Component
    class JpaCriteriaApiCorsProfileAdaptor {
        public SumPagedRep<CORSProfile> execute(CORSProfileQuery query) {
            QueryUtility.QueryContext<CORSProfile> queryContext = QueryUtility.prepareContext(CORSProfile.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), CORSProfile_.CORS_ID, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getDomainIdOrder(CORSProfile_.CORS_ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }

    }
}
