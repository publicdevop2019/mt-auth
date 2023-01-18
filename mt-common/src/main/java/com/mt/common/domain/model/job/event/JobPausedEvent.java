package com.mt.common.domain.model.job.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.JobDetail;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JobPausedEvent extends DomainEvent {
    public static final String JOB_PAUSED = "job_paused";
    public static final String name = "JOB_PAUSED";
    public JobPausedEvent(JobDetail job) {
       super(job.getJobId());
        setTopic(JOB_PAUSED);
        setName(name);
    }
}
