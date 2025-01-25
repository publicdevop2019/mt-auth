package com.mt.common.domain.model.job;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.event.JobPaused;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * distributed job entity, should not be cached to avoid wrong state
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class JobDetail extends Auditable implements Serializable {
    @Setter(AccessLevel.PRIVATE)
    private String name;
    private JobStatus lastStatus;
    private JobType type;
    private Integer failureCount;
    private Integer maxLockAcquireFailureAllowed;
    private boolean isCreate = false;
    /**
     * milliseconds that allowed for job to be not executed,
     * will send notification to admin when this limit reached
     */
    private Long minimumIdleTimeMilli;
    private String failureReason;
    private Integer failureAllowed;
    @Setter
    private Boolean notifiedAdmin;
    private Long lastExecution;
    @Setter(AccessLevel.PRIVATE)
    private JobId jobId;

    public static JobDetail cloneJobFrom(JobDetail job, long instanceId) {
        JobDetail jobDetail = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(job);
        jobDetail.setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        jobDetail.setJobId(new JobId());
        jobDetail.setName(job.getName() + "_" + instanceId);
        jobDetail.isCreate = true;
        return jobDetail;
    }

    public static JobDetail fromDatabaseRow(Long id, Long createdAt, String createdBy,
                                            Long modifiedAt, String modifiedBy, Integer version,
                                            String name, JobStatus lastStatus, JobType type,
                                            Integer failureCount, String failureReason,
                                            Integer failureAllowed,
                                            Integer maxLockAcquireFailureAllowed,
                                            Boolean notifiedAdmin, Long lastExecution,
                                            JobId domainId, Long minimumIdleTimeMilli) {
        JobDetail jobDetail = new JobDetail();
        jobDetail.setId(id);
        jobDetail.setCreatedAt(createdAt);
        jobDetail.setCreatedBy(createdBy);
        jobDetail.setModifiedAt(modifiedAt);
        jobDetail.setModifiedBy(modifiedBy);
        jobDetail.setVersion(version);
        jobDetail.setName(name);
        jobDetail.lastStatus = lastStatus;
        jobDetail.type = type;
        jobDetail.failureCount = failureCount;
        jobDetail.failureReason = failureReason;
        jobDetail.failureAllowed = failureAllowed;
        jobDetail.maxLockAcquireFailureAllowed = maxLockAcquireFailureAllowed;
        jobDetail.notifiedAdmin = notifiedAdmin;
        jobDetail.lastExecution = lastExecution;
        jobDetail.jobId = domainId;
        jobDetail.minimumIdleTimeMilli = minimumIdleTimeMilli;
        return jobDetail;
    }

    public boolean isPaused() {
        return failureCount >= failureAllowed;
    }

    public JobDetail recordJobFailure() {
        JobDetail jobDetail = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        jobDetail.failureCount++;
        jobDetail.failureReason = "JOB_EXECUTION";
        jobDetail.lastExecution = Instant.now().toEpochMilli();
        jobDetail.lastStatus = JobStatus.FAILURE;
        return jobDetail;
    }

    public JobDetail executeSuccess() {
        JobDetail jobDetail = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        jobDetail.failureCount = 0;
        jobDetail.failureReason = null;
        jobDetail.lastExecution = Instant.now().toEpochMilli();
        jobDetail.lastStatus = JobStatus.SUCCESS;
        jobDetail.notifiedAdmin = false;
        return jobDetail;
    }

    public JobDetail reset() {
        JobDetail jobDetail = CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        jobDetail.failureCount = 0;
        jobDetail.notifiedAdmin = false;
        jobDetail.failureReason = null;
        return jobDetail;
    }

    public DomainEvent handleJobExecutionException() {
        JobDetail jobDetail = recordJobFailure();
        CommonDomainRegistry.getJobRepository().update(this, jobDetail);
        if (jobDetail.isPaused() && !getNotifiedAdmin()) {
            log.warn("notify admin about job paused");
            return new JobPaused(this);
        }
        return null;
    }

    public boolean starving() {
        long idleTime = Instant.now().toEpochMilli() - this.lastExecution;
        return idleTime > this.minimumIdleTimeMilli;
    }

    public boolean sameAs(JobDetail o) {
        return Objects.equals(name, o.name) && lastStatus == o.lastStatus &&
            type == o.type && Objects.equals(failureCount, o.failureCount) &&
            Objects.equals(maxLockAcquireFailureAllowed,
                o.maxLockAcquireFailureAllowed) &&
            Objects.equals(minimumIdleTimeMilli, o.minimumIdleTimeMilli) &&
            Objects.equals(failureReason, o.failureReason) &&
            Objects.equals(failureAllowed, o.failureAllowed) &&
            Objects.equals(notifiedAdmin, o.notifiedAdmin) &&
            Objects.equals(lastExecution, o.lastExecution) &&
            Objects.equals(jobId, o.jobId);
    }
}
