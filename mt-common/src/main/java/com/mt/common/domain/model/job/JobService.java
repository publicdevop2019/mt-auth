package com.mt.common.domain.model.job;

public interface JobService {
    void execute(String jobName, Runnable jobFn);
}
