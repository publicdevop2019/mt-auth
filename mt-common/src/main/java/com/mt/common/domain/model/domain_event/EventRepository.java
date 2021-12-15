package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.List;
import java.util.Optional;

public interface EventRepository {

    List<StoredEvent> allStoredEventsSince(long lastId);

    void append(DomainEvent aDomainEvent);

    Optional<StoredEvent> getById(long id);

    SumPagedRep<StoredEvent> query(StoredEventQuery storedEventQuery);
}
