package com.mt.common.application.job;

import com.mt.common.domain.model.job.JobDetail;
import lombok.Getter;

@Getter
public class JobDetailCardRepresentation {
    private final String name;
    private final Integer maxLockAcquireFailureAllowed;
    private final String type;
    private final Integer failureCount;
    private final String failureReason;
    private final Integer failureAllowed;
    private final Long minimumIdleTimeAllowed;
    private final Boolean notifiedAdmin;
    private String lastStatus;
    private String id;
    private Long lastExecution;

    public JobDetailCardRepresentation(JobDetail j) {
        this.id = j.getJobId().getDomainId();
        this.name = j.getName();
        if (j.getLastExecution() != null) {
            this.lastExecution = j.getLastExecution();
        }
        if (j.getLastStatus() != null) {
            this.lastStatus = j.getLastStatus().name();
        }
        this.type = j.getType().name();
        this.failureCount = j.getFailureCount();
        this.failureReason = j.getFailureReason();
        this.failureAllowed = j.getFailureAllowed();
        this.maxLockAcquireFailureAllowed = j.getMaxLockAcquireFailureAllowed();
        this.minimumIdleTimeAllowed = j.getMinimumIdleTimeMilli();
        this.notifiedAdmin = j.getNotifiedAdmin();
    }
}
