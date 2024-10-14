package com.mt.common.domain.model.job.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.JobDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class JobThreadStarving extends DomainEvent {
    public static final String JOB_THREAD_STARVING = "job_thread_starving";
    public static final String name = "JOB_THREAD_STARVING";
    private Long instanceId;
    private String jobName;

    {

        setTopic(JOB_THREAD_STARVING);
        setName(name);
    }

    public JobThreadStarving(JobDetail jobDetail, long instanceId) {
        super(jobDetail.getJobId());
        this.instanceId = instanceId;
        this.jobName = jobDetail.getName();
    }
}
