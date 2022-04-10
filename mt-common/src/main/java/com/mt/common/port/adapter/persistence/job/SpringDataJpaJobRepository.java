package com.mt.common.port.adapter.persistence.job;

import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobDetail_;
import com.mt.common.domain.model.job.JobQuery;
import com.mt.common.domain.model.job.JobRepository;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.port.adapter.persistence.CommonQueryBuilderRegistry;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

public interface SpringDataJpaJobRepository extends JobRepository, JpaRepository<JobDetail, Long> {
    default Set<JobDetail> query(JobQuery all) {
        return QueryUtility
            .getAllByQuery(e -> CommonQueryBuilderRegistry.getJobAdaptor().execute(e), all);
    }

    default void store(JobDetail jobDetail) {
        save(jobDetail);
    }

    @Component
    class JpaCriteriaApiJobAdaptor {
        public SumPagedRep<JobDetail> execute(JobQuery query) {
            QueryUtility.QueryContext<JobDetail> queryContext =
                QueryUtility.prepareContext(JobDetail.class, query);
            Optional.ofNullable(query.getName()).ifPresent(e -> {
                QueryUtility
                    .addEnumLiteralEqualPredicate(Collections.singleton(e), JobDetail_.NAME,
                        queryContext);
            });
            Order order = null;
            if (query.getSort().isById()) {
                order = QueryUtility.getDomainIdOrder(JobDetail_.JOB_ID, queryContext,
                    query.getSort().isAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }
    }
}
