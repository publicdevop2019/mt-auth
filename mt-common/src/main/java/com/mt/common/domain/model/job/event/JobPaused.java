package com.mt.common.domain.model.job.event;

import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.job.JobDetail;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JobPaused extends DomainEvent {
    public static final String JOB_PAUSED = "job_paused";
    public static final String name = "JOB_PAUSED";
    {
        setTopic(JOB_PAUSED);
        setName(name);

    }
    public JobPaused(JobDetail job) {
       super(job.getJobId());
    }
}
