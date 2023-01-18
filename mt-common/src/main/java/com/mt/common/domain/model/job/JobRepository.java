package com.mt.common.domain.model.job;

import java.util.Optional;
import java.util.Set;

public interface JobRepository {
    Set<JobDetail> getByQuery(JobQuery all);
    Optional<JobDetail> getByName(String name);

    void store(JobDetail jobDetail);

    Optional<JobDetail> getById(JobId jobId);
}
