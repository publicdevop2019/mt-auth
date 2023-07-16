package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.model.restful.SumPagedRep;

public interface DomainEventRepository {

    void append(StoredEvent event);

    StoredEvent getById(long id);

    SumPagedRep<StoredEvent> query(StoredEventQuery storedEventQuery);
}
