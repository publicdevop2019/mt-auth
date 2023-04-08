package com.mt.common.domain.model.domain_event;

import static com.mt.common.domain.model.constant.AppInfo.EVENT_SCAN_JOB_NAME;
import static com.mt.common.domain.model.constant.AppInfo.MISSED_EVENT_SCAN_JOB_NAME;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.notification.PublishedEventTracker;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class EventApplicationServiceScheduler {

    /**
     * if unlock failed then event tracker will not update,
     * same event will get emit multiple times due to this unlock issue
     * add initial delay due to some mq binding is dynamically created, immediate run will cause unroutable error
     */
    @Scheduled(cron = "0/2 * * ? * *")
    public void streaming() {
        log.trace("start of scheduled task 5");
        CommonDomainRegistry.getJobService()
            .execute(EVENT_SCAN_JOB_NAME,
                () -> {
                    PublishedEventTracker eventTracker =
                        CommonDomainRegistry.getPublishedEventTrackerRepository()
                            .publishedNotificationTracker();
                    List<StoredEvent> storedEvents =
                        CommonDomainRegistry.getDomainEventRepository()
                            .top50StoredEventsSince(eventTracker.getLastPublishedId());
                    if (!storedEvents.isEmpty()) {
                        log.trace("publish event since id {}",
                            eventTracker.getLastPublishedId());
                        log.trace("total domain event found {}", storedEvents.size());
                        for (StoredEvent event : storedEvents) {
                            log.trace("publishing event {} with id {}", event.getName(),
                                event.getId());
                            CommonDomainRegistry.getEventStreamService()
                                .next(event);
                        }
                        CommonDomainRegistry.getPublishedEventTrackerRepository()
                            .trackMostRecentPublishedNotification(eventTracker, storedEvents);
                    }
                }, true, 30);
    }

    @Scheduled(cron = "0 */5 * ? * *")
    public void checkNotSend() {
        log.trace("start of scheduled task 6");
        CommonDomainRegistry.getJobService()
            .execute(MISSED_EVENT_SCAN_JOB_NAME,
                () -> {
                    log.debug("running task for not send event");
                    Set<StoredEvent> allByQuery = QueryUtility
                        .getAllByQuery(
                            e -> CommonDomainRegistry.getDomainEventRepository().query(e),
                            StoredEventQuery.notSend());
                    if (!allByQuery.isEmpty()) {
                        log.debug("start of publish not send event");
                        for (StoredEvent event : allByQuery) {
                            log.debug("publishing event {} with id {}", event.getName(),
                                event.getId());
                            CommonDomainRegistry.getEventStreamService()
                                .next(event);
                            event.sendToMQ();
                        }
                        log.debug("end of publish not send event");
                    }
                }, true, 1);
    }

}
