package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.notification.PublishedEventTracker;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@EnableScheduling
public class EventApplicationServiceScheduler {

    @Value("${spring.application.name}")
    private String appName;

    @Transactional
    @Scheduled(fixedRateString = "${fixedRate.in.milliseconds.notification}")
    public void streaming() {
        PublishedEventTracker eventTracker =
                CommonDomainRegistry.getPublishedEventTrackerRepository().publishedNotificationTracker();
        List<StoredEvent> storedEvents = CommonDomainRegistry.getDomainEventRepository().top50StoredEventsSince(eventTracker.getLastPublishedId());
        if (!storedEvents.isEmpty()) {
            log.trace("publish event since id {}", eventTracker.getLastPublishedId());
            log.trace("total domain event found {}", storedEvents.size());
            for (StoredEvent event : storedEvents) {
                log.trace("publishing event {} with id {}",event.getName(), event.getId());
                CommonDomainRegistry.getEventStreamService().next(appName, event.isInternal(), event.getTopic(), event);
                event.sendToMQ();
            }
            CommonDomainRegistry.getPublishedEventTrackerRepository()
                    .trackMostRecentPublishedNotification(eventTracker, storedEvents);
        }
    }

    @Transactional
    @Scheduled(fixedRate = 5*60*1000,initialDelay = 60*1000)
    public void checkNotSend() {
        log.debug("running task for not send event");
        Set<StoredEvent> allByQuery = QueryUtility.getAllByQuery(e -> CommonDomainRegistry.getDomainEventRepository().query(e), StoredEventQuery.notSend());
        if (!allByQuery.isEmpty()) {
            log.debug("start of publish not send event");
            for (StoredEvent event : allByQuery) {
                log.debug("publishing event {} with id {}",event.getName(), event.getId());
                CommonDomainRegistry.getEventStreamService().next(appName, event.isInternal(), event.getTopic(), event);
                event.sendToMQ();
            }
            log.debug("end of publish not send event");
        }
    }

}
