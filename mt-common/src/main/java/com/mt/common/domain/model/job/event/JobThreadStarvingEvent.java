package com.mt.common.domain.model.job.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.JobDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class JobThreadStarvingEvent extends DomainEvent {
    public static final String JOB_STARVING = "job_thread_starving";
    public static final String name = "JOB_THREAD_STARVING";
    private long instanceId;
    private String jobName;

    {

        setTopic(JOB_STARVING);
        setName(name);
    }

    public JobThreadStarvingEvent(JobDetail jobDetail, long instanceId) {
        super(jobDetail.getJobId());
        this.instanceId = instanceId;
        this.jobName = jobDetail.getName();
    }
}
