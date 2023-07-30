package com.mt.common.application.domain_event;

import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.domain_event.event.RejectedMsgReceivedEvent;
import com.mt.common.domain.model.domain_event.event.UnrountableMsgReceivedEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.time.Instant;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class StoredEventApplicationService {

    private static final String AGGREGATE_NAME = "STORED_EVENT";

    public void retry(long id) {
        StoredEvent byId = CommonDomainRegistry.getDomainEventRepository().getById(id);
        CommonDomainRegistry.getEventStreamService().next(byId);
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
    public void markAsUnroutable(StoredEvent event) {
        Long id = event.getId();
        if (id != null) {
            CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(String.valueOf(id), (context) -> {
                        context
                            .append(new UnrountableMsgReceivedEvent(event));
                        StoredEvent byId =
                            CommonDomainRegistry.getDomainEventRepository().getById(event.getId());
                        byId.markAsUnroutable();
                        return null;
                    },
                    AGGREGATE_NAME);
        } else {
            log.warn(
                "non stored event like app_start are un-routable (which maybe ok, due to no instance of other apps) detail: {}",
                event);
        }

    }

    /**
     * record rejected msg, no idempotency required bcz by nature it is.
     *
     * @param event rejected event
     */
    public void recordRejectedEvent(StoredEvent event) {
        Long id = event.getId();
        if (id != null) {
            CommonDomainRegistry.getTransactionService().transactionalEvent((context)->{
                context
                    .append(new RejectedMsgReceivedEvent(event));
                StoredEvent first = CommonDomainRegistry.getDomainEventRepository()
                    .getById(event.getId());
                first.markAsRejected();
            });
        } else {
            log.warn(
                "none-stored event are being rejected, event name is {}",
                event.getName());
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
            if (log.isDebugEnabled()) {
                long epochSecond = Instant.now().toEpochMilli();
                Long timestamp = storedEvent.getTimestamp();
                log.debug("marking event with id = {} as sent, time taken to emit is {} milli", id,
                    epochSecond - timestamp);
            }
            StoredEvent byId = CommonDomainRegistry.getDomainEventRepository().getById(id);
            byId.sendToMQ();
        } else {
            log.info(
                "none-stored event are being marked as sent, which is ok");
        }
    }
}
