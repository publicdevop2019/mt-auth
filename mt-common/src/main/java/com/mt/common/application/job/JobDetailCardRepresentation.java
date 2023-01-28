package com.mt.common.application.job;

import com.mt.common.domain.model.job.JobDetail;
import lombok.Getter;

@Getter
public class JobDetailCardRepresentation {
    private final String name;
    private final int lockAcquireFailureCount;
    private final int maxLockAcquireFailureAllowed;
    private String lastStatus;
    private String id;
    private final String type;
    private final int failureCount;
    private final String failureReason;
    private final int failureAllowed;
    private final boolean notifiedAdmin;
    private long lastExecution;

    public JobDetailCardRepresentation(JobDetail j) {
        this.id = j.getJobId().getDomainId();
        this.name = j.getName();
        if (j.getLastExecution() != null) {
            this.lastExecution = j.getLastExecution().getTime();
        }
        if (j.getLastStatus() != null) {
            this.lastStatus = j.getLastStatus().name();
        }
        this.type = j.getType().name();
        this.failureCount = j.getFailureCount();
        this.failureReason = j.getFailureReason();
        this.failureAllowed = j.getFailureAllowed();
        this.lockAcquireFailureCount = j.getLockAcquireFailureCount();
        this.maxLockAcquireFailureAllowed = j.getMaxLockAcquireFailureAllowed();
        this.notifiedAdmin = j.isNotifiedAdmin();
    }
}
