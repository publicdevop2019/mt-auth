package com.mt.common.application.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class StoredEventApplicationService {
    public void retry(long id) {
        Optional<StoredEvent> byId = CommonDomainRegistry.getDomainEventRepository().getById(id);
        if (byId.isPresent()) {
            CommonDomainRegistry.getEventStreamService().next(byId.get());
        } else {
            throw new IllegalArgumentException("unable to find stored event with id " + id);
        }
    }

    public SumPagedRep<StoredEvent> query(String queryParam, String pageParam, String skipCount) {
        StoredEventQuery storedEventQuery = new StoredEventQuery(queryParam, pageParam, skipCount);
        return CommonDomainRegistry.getDomainEventRepository().query(storedEventQuery);
    }

    public SumPagedRep<StoredEvent> query(Set<String> names, String queryParam, String pageParam,
                                          String skipCount) {
        StoredEventQuery storedEventQuery =
            new StoredEventQuery(names, queryParam, pageParam, skipCount);
        return CommonDomainRegistry.getDomainEventRepository().query(storedEventQuery);
    }
}
