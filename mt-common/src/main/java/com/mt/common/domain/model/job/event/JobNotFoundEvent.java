package com.mt.common.domain.model.job.event;

import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;

public class JobNotFoundEvent extends DomainEvent {
    public static final String JOB_NOT_FOUND = "job_not_found";
    public static final String name = "JOB_NOT_FOUND";
    public JobNotFoundEvent() {
        super(new AnyDomainId());
        setTopic(JOB_NOT_FOUND);
        setName(name);
    }
}
