package com.mt.common.port.adapter.persistence.job;

import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobDetail_;
import com.mt.common.domain.model.job.JobId;
import com.mt.common.domain.model.job.JobQuery;
import com.mt.common.domain.model.job.JobRepository;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.port.adapter.persistence.CommonQueryBuilderRegistry;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

public interface SpringDataJpaJobRepository extends JobRepository, JpaRepository<JobDetail, Long> {
    default Set<JobDetail> getByQuery(JobQuery all) {
        return QueryUtility
            .getAllByQuery(e -> CommonQueryBuilderRegistry.getJobAdaptor().execute(e), all);
    }

    default Optional<JobDetail> getByName(String name) {
        return CommonQueryBuilderRegistry.getJobAdaptor().execute(JobQuery.byName(name))
            .findFirst();
    }


    default JobDetail getById(JobId id) {
        return CommonQueryBuilderRegistry.getJobAdaptor().execute(new JobQuery(id)).findFirst().orElse(null);
    }


    default void store(JobDetail jobDetail) {
        save(jobDetail);
    }

    default void notifyAdmin(JobId jobId){
        _notifyAdmin(jobId);
    }

    @Modifying
    @Query("update #{#entityName} n set n.notifiedAdmin=true where n.jobId = ?1")
    void _notifyAdmin(JobId jobId);

    @Component
    class JpaCriteriaApiJobAdaptor {
        public SumPagedRep<JobDetail> execute(JobQuery query) {
            QueryUtility.QueryContext<JobDetail> queryContext =
                QueryUtility.prepareContext(JobDetail.class, query);
            Optional.ofNullable(query.getName()).ifPresent(e -> {
                QueryUtility
                    .addStringEqualPredicate(e, JobDetail_.NAME,
                        queryContext);
            });
            Optional.ofNullable(query.getId()).ifPresent(e -> {
                QueryUtility
                    .addDomainIdIsPredicate(e.getDomainId(), JobDetail_.JOB_ID,
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
