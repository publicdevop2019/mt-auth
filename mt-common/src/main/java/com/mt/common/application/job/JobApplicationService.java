package com.mt.common.application.job;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.domain.model.job.JobId;
import com.mt.common.domain.model.job.JobQuery;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void resetJob(String id) {
        Optional<JobDetail> byId = CommonDomainRegistry.getJobRepository().getById(new JobId(id));
        byId.ifPresent(e -> {
            e.reset();
            CommonDomainRegistry.getJobService().reset(e.getName());
        });
    }
}
