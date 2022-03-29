package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.List;
import java.util.Optional;

public interface DomainEventRepository {

    List<StoredEvent> top50StoredEventsSince(long lastId);

    void append(DomainEvent event);

    Optional<StoredEvent> getById(long id);

    SumPagedRep<StoredEvent> query(StoredEventQuery storedEventQuery);
}
