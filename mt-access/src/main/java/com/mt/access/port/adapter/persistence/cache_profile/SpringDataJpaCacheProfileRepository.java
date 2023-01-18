package com.mt.access.port.adapter.persistence.cache_profile;

import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.CacheProfileQuery;
import com.mt.access.domain.model.cache_profile.CacheProfileRepository;
import com.mt.access.domain.model.cache_profile.CacheProfile_;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaCacheProfileRepository
    extends CacheProfileRepository, JpaRepository<CacheProfile, Long> {
    default Optional<CacheProfile> cacheProfileOfId(CacheProfileId id) {
        return cacheProfileOfQuery(new CacheProfileQuery(id)).findFirst();
    }

    default void add(CacheProfile cacheProfile) {
        save(cacheProfile);
    }

    default void remove(CacheProfile cacheProfile) {
        cacheProfile.softDelete();
        save(cacheProfile);
    }

    default SumPagedRep<CacheProfile> cacheProfileOfQuery(CacheProfileQuery query) {
        return QueryBuilderRegistry.getCacheProfileAdaptor().execute(query);
    }

    @Component
    class JpaCriteriaApiCacheProfileAdaptor {
        public SumPagedRep<CacheProfile> execute(CacheProfileQuery query) {
            QueryUtility.QueryContext<CacheProfile> queryContext =
                QueryUtility.prepareContext(CacheProfile.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(
                e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                CacheProfile_.CACHE_PROFILE_ID, queryContext));
            Order order = null;
            if (query.getSort().isById()) {
                order = QueryUtility.getDomainIdOrder(CacheProfile_.CACHE_PROFILE_ID, queryContext,
                    query.getSort().isAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }

    }
}
