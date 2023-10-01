package com.mt.common.application.job;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobId;
import com.mt.common.domain.model.job.JobQuery;
import com.mt.common.domain.model.job.JobType;
import com.mt.common.domain.model.validate.Validator;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class JobApplicationService {

    public List<JobDetailCardRepresentation> currentJobs() {
        Set<JobDetail> byQuery = CommonDomainRegistry.getJobRepository().getByQuery(JobQuery.all());
        Set<JobDetailCardRepresentation> collect =
            byQuery.stream().map(JobDetailCardRepresentation::new).collect(Collectors.toSet());
        return collect.stream().sorted(Comparator.comparing(JobDetailCardRepresentation::getName))
            .collect(
                Collectors.toList());
    }

    public void resetJob(String id) {
        CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
            JobDetail current = CommonDomainRegistry.getJobRepository().getById(new JobId(id));
            Validator.notNull(current);
            JobDetail update = current.reset();
            CommonDomainRegistry.getJobRepository().update(current, update);
        });
    }

    /**
     * reset job lock in case of deadlock,
     * this should be used with caution.
     *
     * @param jobId job id
     */
    public void resetJobLock(String jobId) {
        JobDetail jobDetail =
            CommonDomainRegistry.getJobRepository().getById(new JobId(jobId));
        Validator.notNull(jobDetail);
        if (jobDetail.getType().equals(JobType.CLUSTER)) {
            CommonDomainRegistry.getJobService().resetLock(jobDetail.getName());
        }
    }
}
