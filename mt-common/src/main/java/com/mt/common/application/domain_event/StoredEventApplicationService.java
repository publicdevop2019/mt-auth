package com.mt.common.application.domain_event;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.domain_event.event.UnrountableMsgReceivedEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class StoredEventApplicationService {

    private static final String AGGREGATE_NAME = "STORED_EVENT";

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

    /**
     * mark target event as unroutable and create unroutable event received event
     * need to note that idempotency is required.
     *
     * @param event unroutable event
     */
    @Transactional
    public void markAsUnroutable(StoredEvent event) {
        Long id = event.getId();
        if (id != null && id != -1) {
            CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(String.valueOf(id), (ignored) -> {
                        CommonDomainRegistry.getDomainEventRepository()
                            .append(new UnrountableMsgReceivedEvent(event));
                        CommonDomainRegistry.getDomainEventRepository().getById(event.getId())
                            .ifPresent(
                                StoredEvent::markAsUnroutable);
                        return null;
                    },
                    AGGREGATE_NAME);
        } else {
            log.warn(
                "non stored event like app_start are un-routable (which maybe ok) detail: {}",
                event);
        }

    }

    /**
     * mark event as sent, no idempotency required bcz by nature it is.
     *
     * @param storedEvent stored event
     */
    @Transactional
    public void markAsSent(StoredEvent storedEvent) {
        Long id = storedEvent.getId();
        if (id != null) {
            log.debug("marking event with id={} as sent", id);
            CommonDomainRegistry.getDomainEventRepository().getById(id)
                .ifPresentOrElse(
                    StoredEvent::sendToMQ,
                    () -> {
                        if (!AnyDomainId.isSystemId(storedEvent.getDomainId())) {
                            log.error(
                                "event with id {} not found, which should not happen",
                                id);
                        }
                    }
                );
        }
    }
}
