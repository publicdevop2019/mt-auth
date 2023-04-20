package com.mt.access.port.adapter.persistence.cache_profile;

import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.CacheProfileQuery;
import com.mt.access.domain.model.cache_profile.CacheProfileRepository;
import com.mt.access.domain.model.cache_profile.CacheProfile_;
import com.mt.access.domain.model.cors_profile.CorsProfile_;
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
    default Optional<CacheProfile> id(CacheProfileId id) {
        return query(new CacheProfileQuery(id)).findFirst();
    }

    default void add(CacheProfile cacheProfile) {
        save(cacheProfile);
    }

    default void remove(CacheProfile cacheProfile) {
        delete(cacheProfile);
    }

    default SumPagedRep<CacheProfile> query(CacheProfileQuery query) {
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
            Optional.ofNullable(query.getProjectId()).ifPresent(e -> QueryUtility.addDomainIdIsPredicate(
                e.getDomainId(),
                CorsProfile_.PROJECT_ID, queryContext));
            Order order = null;
            if (query.getSort().isById()) {
                order = QueryUtility.getDomainIdOrder(CacheProfile_.CACHE_PROFILE_ID, queryContext,
                    query.getSort().isAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }

    }
}
