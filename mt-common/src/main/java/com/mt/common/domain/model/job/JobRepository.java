package com.mt.common.domain.model.job;

import java.util.Set;

public interface JobRepository {
    Set<JobDetail> query(JobQuery all);

    void store(JobDetail jobDetail);
}
