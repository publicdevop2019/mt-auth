package com.mt.access.port.adapter.persistence.cors_profile;

import com.mt.access.domain.model.cors_profile.CORSProfile_;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.CorsProfileQuery;
import com.mt.access.domain.model.cors_profile.CorsProfileRepository;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

public interface SpringDataJpaCorsProfileRepository
    extends CorsProfileRepository, JpaRepository<CorsProfile, Long> {
    default Optional<CorsProfile> corsProfileOfId(CorsProfileId id) {
        return corsProfileOfQuery(new CorsProfileQuery(id)).findFirst();
    }

    default void add(CorsProfile corsProfile) {
        save(corsProfile);
    }

    default void remove(CorsProfile corsProfile) {
        corsProfile.softDelete();
        save(corsProfile);
    }

    default SumPagedRep<CorsProfile> corsProfileOfQuery(CorsProfileQuery query) {
        return QueryBuilderRegistry.getCorsProfileAdaptor().execute(query);
    }

    @Component
    class JpaCriteriaApiCorsProfileAdaptor {
        public SumPagedRep<CorsProfile> execute(CorsProfileQuery query) {
            QueryUtility.QueryContext<CorsProfile> queryContext =
                QueryUtility.prepareContext(CorsProfile.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(
                e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                CORSProfile_.CORS_ID, queryContext));
            Order order = null;
            if (query.getSort().isById()) {
                order = QueryUtility
                    .getDomainIdOrder(CORSProfile_.CORS_ID, queryContext, query.getSort().isAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }

    }
}
