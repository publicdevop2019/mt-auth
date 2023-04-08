package com.mt.common.domain.model.job;

public interface DistributedJobService {
    void execute(String jobName, Runnable jobFn, boolean transactional,int ignoreCount);

    void resetLock(String jobName);
}
