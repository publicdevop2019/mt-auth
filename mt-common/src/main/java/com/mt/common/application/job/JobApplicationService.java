package com.mt.common.application.job;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobQuery;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class JobApplicationService {
    public Set<JobDetail> currentJobs() {
        return CommonDomainRegistry.getJobRepository().query(JobQuery.all());
    }

    public void createOrUpdateJob(JobDetail jobDetail) {
        Optional<JobDetail> jobDetail1 =
            CommonDomainRegistry.getJobRepository().query(new JobQuery(jobDetail.getName()))
                .stream()
                .findFirst();
        if (jobDetail1.isPresent()) {
            JobDetail jobDetail2 = jobDetail1.get();
            jobDetail2.updateStatus(jobDetail);
            CommonDomainRegistry.getJobRepository().store(jobDetail2);
        } else {
            CommonDomainRegistry.getJobRepository().store(jobDetail);
        }
    }
}
