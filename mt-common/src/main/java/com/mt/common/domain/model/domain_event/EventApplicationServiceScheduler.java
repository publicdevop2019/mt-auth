package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.notification.PublishedEventTracker;
import com.mt.common.domain.model.notification.PublishedEventTrackerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        List<StoredEvent> storedEvents = CommonDomainRegistry.getEventRepository().allStoredEventsSince(eventTracker.getLastPublishedId());
        if (!storedEvents.isEmpty()) {
            log.trace("publish event since id {}", eventTracker.getLastPublishedId());
            for (StoredEvent event : storedEvents) {
                log.trace("publishing event {} with id {}",event.getName(), event.getId());
                CommonDomainRegistry.getEventStreamService().next(appName, event.isInternal(), event.getTopic(), event);
            }
            CommonDomainRegistry.getPublishedEventTrackerRepository()
                    .trackMostRecentPublishedNotification(eventTracker, storedEvents);
        }
    }

}
