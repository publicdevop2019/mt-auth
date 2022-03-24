//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.mt.common.port.adapter.persistence.domain_event;

import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.notification.PublishedEventTracker;
import com.mt.common.domain.model.notification.PublishedEventTrackerRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * improved event tracker, rarely it will not send events properly .e.g insert 1,2,3,5 and 4 later, 4 will be missed
 */
@Repository
public interface SpringDataJpaPublishedEventTrackerRepository extends PublishedEventTrackerRepository, JpaRepository<PublishedEventTracker, Long> {
    Logger log = LoggerFactory.getLogger(SpringDataJpaPublishedEventTrackerRepository.class);

    default PublishedEventTracker publishedNotificationTracker() {
        Iterable<PublishedEventTracker> all = findAll();
        List<PublishedEventTracker> objects = new ArrayList<>(1);
        all.forEach(objects::add);
        return objects.isEmpty() ? new PublishedEventTracker() : objects.get(0);
    }

    default void trackMostRecentPublishedNotification(PublishedEventTracker tracker, List<StoredEvent> events) {
        int lastIndex = events.size() - 1;
        if (lastIndex >= 0) {
            long mostRecentId = events.get(lastIndex).getId();
            tracker.setLastPublishedId(mostRecentId);
            save(tracker);
        }
    }

}
